package it.dtk

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.Terminated
import akka.actor.ReceiveTimeout
import it.dtk.db.{ DBManager, News }
import scala.util.{ Success, Failure }
import org.joda.time.DateTime
import it.dtk.db.DBManager
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import it.dtk.util.URLUtil
import scala.concurrent.duration._

object WebSiteController {

  case class Start(vectorUrl: Vector[String] = Vector(), currentIndex: Int = 1)

  case class Job(url: String, index: Int, running: Boolean = false)

  case class Done(id: String, extractedUrls: Vector[String])

  case class Fail(id: String, currentIndex: Int, extractedUrls: Vector[String])

  case object Status

  case object Waiting
  
  case object Running

}

/**
 * @author Fabio
 *
 */
abstract class WebSiteController(val id: String, val dbActor: ActorRef, val routerHttpGetter: ActorRef) extends Actor with ActorLogging {

  import WebSiteController._

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = -1, loggingEnabled = true) {

    case e: Exception =>
      log.error("got exception in DataRecordExtractor {}",e.getMessage())
      SupervisorStrategy.Restart

    case e: Throwable =>
      log.error("got exception in DataRecordExtractor {}",e.getMessage())
      SupervisorStrategy.Restart
  }

  // Max call duration
  context.setReceiveTimeout(60.seconds)

  val baseUrl: String
  val maxIndex: Int

  def dataRecordExtractorProps(): Props

  def composeUrl(currentIndex: Int): String

  def mainContentExtractorProps(news: News): Props = Props(classOf[MainContentExtractor], news, routerHttpGetter)

  val drActor = context.actorOf(dataRecordExtractorProps())

  def receive = waiting

  val waiting: Receive = {

    case Start(stopUrls, currentIndex) =>
      context.become(runNext(stopUrls, currentIndex, Vector[String](), sender))

    case Status =>
      Waiting
  }

  def runNext(stopUrls: Vector[String], currentIndex: Int, extractedUrls: Vector[String], jobSender: ActorRef): Receive = {

    if (currentIndex > maxIndex) {
      log.info("all done waiting for new jobs")
      jobSender ! Done(id, extractedUrls)
      waiting
    } else {
      val url = composeUrl(currentIndex)
      log.info("start processing next job")

      drActor ! DataRecordExtractor.Extract(url)

      running(stopUrls, currentIndex, extractedUrls, jobSender)
    }
  }

  def running(stopUrls: Vector[String], currentIndex: Int, extractedUrls: Vector[String], jobSender: ActorRef): Receive = {

    case Status =>
      Running

    case DataRecordExtractor.DataRecords(url, date, records) =>

      val normalizedRecords = records.map(r => {
          URLUtil.normalize(baseUrl, r.newsUrl) match {
            case Success(normUrl) => r.copy(newsUrl = normUrl)
            case Failure(_) => r
          }
      })
      
      val filteredRecords = normalizedRecords.takeWhile(r => !stopUrls.contains(r.newsUrl))

      filteredRecords.foreach(r => {
        log.info("Getting main article content for URL {}", r.newsUrl)

        val recordNews = News(None, Some(baseUrl), Some(r.newsUrl), Some(r.title), Some(r.summary), Some(date))
        context.watch(context.actorOf(mainContentExtractorProps(recordNews)))
      })

      val urls = extractedUrls ++ filteredRecords.map(_.newsUrl).toVector

      val nextStatus = if (filteredRecords.size < records.size)
        runNext(stopUrls, maxIndex + 1, urls, jobSender: ActorRef)
      else
        running(stopUrls, currentIndex, urls, jobSender: ActorRef)

      context.become(nextStatus)

    case MainContentExtractor.Result(news) =>
      log.info("Got main article content for URL {}", news.urlNews)
      dbActor ! DBManager.InsertNews(news)

    case DBManager.FailHandlingNews(news, ex) =>
      log.error("Error inserting the new in the db {} with ex", news.urlNews, ex)

    case Terminated(ref) =>
      log.info("Number of children {}", context.children.size)

      if (context.children.size == 1)
        context.become(runNext(stopUrls, currentIndex + 1, extractedUrls, jobSender: ActorRef))
        

    case timeout: ReceiveTimeout =>
      log.info("Failure in the extraction of the website controller with id {}", id)
      //context.children foreach context.stop
      context.parent ! Fail(id, currentIndex, extractedUrls)
  }

}

