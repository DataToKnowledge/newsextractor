package it.dtk

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.Terminated
import akka.actor.ReceiveTimeout
import it.dtk.db.{ DBManager, News }
import scala.util.Failure
import org.joda.time.DateTime
import it.dtk.db.DBManager
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import it.dtk.util.URLUtil

object WebSiteController {

  case class Start(vectorUrl: Vector[String] = Vector())

  case class Job(url: String, index: Int, running: Boolean = false)

  case class Done(extractedUrls: Vector[String])

  case class Fail(url: String)

}

/**
 * @author Fabio
 *
 */
trait WebSiteController extends Actor with ActorLogging {

  import WebSiteController._

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 5) {
    case _: Exception => SupervisorStrategy.Restart
  }

  // Max call duration
  // context.setReceiveTimeout(10.seconds)

  val baseUrl: String
  val maxIndex: Int

  protected val dbActor: ActorRef

  def dataRecordExtractorProps(): Props

  def composeUrl(currentIndex: Int): String

  def mainContentExtractorProps(news: News): Props = Props(classOf[MainContentExtractor], news)

  val drActor = context.actorOf(dataRecordExtractorProps)

  def receive = waiting

  val waiting: Receive = {

    case Start(stopUrls) =>
      context.become(runNext(stopUrls, 1, Vector[String](),sender))
  }

  def runNext(stopUrls: Vector[String], currentIndex: Int, extractedUrls: Vector[String], jobSender: ActorRef): Receive = {

    if (currentIndex > maxIndex) {
      log.info("all done waiting for new jobs")
      jobSender ! Done(extractedUrls)
      waiting
    } else {
      val url = composeUrl(currentIndex)
      log.info("start processing next job")

      drActor ! DataRecordExtractor.Extract(url)

      running(stopUrls, currentIndex, extractedUrls,jobSender)
    }
  }

  def running(stopUrls: Vector[String], currentIndex: Int, extractedUrls: Vector[String],jobSender: ActorRef): Receive = {

    case DataRecordExtractor.DataRecords(url, date, records) =>

      val filteredRecords = records.takeWhile(r => !stopUrls.contains(r.newsUrl))

      val normalizedRecords = records.map(r => {
        val normalizedUrl = if (URLUtil.isRelative(r.newsUrl))
          baseUrl + r.newsUrl
        else
          r.newsUrl

        // FIXME: Issue #21
        //        val recordNews: News = URLUtil.normalize(baseUrl, r.newsUrl) match {
        //          case Success(url) =>
        //            News(None, Some(baseUrl), Some(url), Some(r.title), Some(r.summary), Some(date))
        //
        //          case Failure(ex) =>
        //            News(None, Some(baseUrl), Some(r.newsUrl), Some(r.title), Some(r.summary), Some(date))
        //        }
        r.copy(newsUrl = normalizedUrl)
      })

      normalizedRecords.foreach(r => {
        log.info("Getting main article content for URL {}", r.newsUrl)

        val recordNews = News(None, Some(baseUrl), Some(r.newsUrl), Some(r.title), Some(r.summary), Some(date))
        context.watch(context.actorOf(mainContentExtractorProps(recordNews)))
      })

      val urls = extractedUrls ++ normalizedRecords.map(_.newsUrl).toVector

      val nextStatus = if (normalizedRecords.size < records.size)
        running(stopUrls, maxIndex + 1, urls,jobSender: ActorRef)
      else
        running(stopUrls, currentIndex, urls,jobSender: ActorRef)

      context.become(nextStatus)

    case MainContentExtractor.Result(news) =>
      log.info("Got main article content for URL {}", news.urlNews)
      dbActor ! DBManager.Insert(news)

    case DBManager.Fail(news) =>
      log.info("error inserting the new in the db {}", news.urlNews)

    case Terminated(ref) =>
      log.info("number of children {}", context.children.size)
      
      if (context.children.size == 1)
        context.become(runNext(stopUrls, currentIndex + 1, extractedUrls,jobSender: ActorRef))

    case ReceiveTimeout =>
      log.info("Failure in the extraction of the website {}", baseUrl)
      context.children foreach context.stop
      context.parent ! Fail(baseUrl)
  }

}

