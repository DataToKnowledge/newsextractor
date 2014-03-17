package it.dtk

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.Terminated
import akka.actor.ReceiveTimeout
import it.dtk.db.News
import scala.util.{ Success, Failure }
import it.dtk.db.DBManager
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import it.dtk.util.URLUtil
import scala.concurrent.duration._
import akka.actor.PoisonPill
import akka.actor.OneForOneStrategy

object WebSiteController {

  case class Start(vectorUrl: Vector[String] = Vector(), currentIndex: Int = 1)

  case class Job(url: String, index: Int, running: Boolean = false)

  case class JobUpdate(idController: String, dataRecordUrl: String, extractedRecords: Vector[String])

  case class Done(id: String, extractedUrls: Vector[String])

  case class Fail(id: String, currentIndex: Int, extractedUrls: Vector[String])

  case object Status

  case object Waiting

  case object Running

  def props(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef) =
    Props(classOf[WebSiteController], id, dbActor, routerHttpGetter)

}

/**
 * @author Fabio
 *
 */
abstract class WebSiteController(val id: String, val dbActor: ActorRef, val routerHttpGetter: ActorRef) extends Actor with ActorLogging {
  // Max call duration
  context.setReceiveTimeout(120.seconds)
  val baseUrl: String
  val maxIndex: Int

  def composeUrl(currentIndex: Int): String

  def dataRecordExtractorProps(): Props
  private val drActor = context.actorOf(dataRecordExtractorProps(), self.path.name + "-DataRecord")

  var countContentExtractors = 0
  def mainContentExtractorProps(news: News): Props = Props(classOf[MainContentExtractor], news, routerHttpGetter)

  import WebSiteController._
  
//  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 100.seconds) {
//    case _ => Restart
//  }

  def receive = waiting

  val waiting: Receive = {

    case Start(stopUrls, currentIndex) =>
      log.info("start processing {}", self.path.name)
      context.become(runNext(currentIndex, stopUrls, Vector[String](), sender))

    case Status =>
      Waiting
  }

  def runNext(currentIndex: Int, stopUrls: Vector[String], extractedUrls: Vector[String], jobSender: ActorRef): Receive = {
    if (currentIndex > maxIndex) {
      log.info("End Processing {}", self.path.name)
      jobSender ! Done(id, extractedUrls)
      waiting
    } else {
      log.info("Processing Page {} for the Actor {}", currentIndex, self.path.name)
      val nextUrl = composeUrl(currentIndex)
      drActor ! DataRecordExtractor.Extract(nextUrl)
      running(currentIndex, stopUrls, extractedUrls, jobSender, 0)
    }
  }

  def running(currentIndex: Int, stopUrls: Vector[String], extractedUrls: Vector[String], jobSender: ActorRef, runningExtractions: Int): Receive = {

    case Status =>
      sender ! Running

    case DataRecordExtractor.DataRecords(url, records) =>
      log.info("got {} data records from {}",records.size, sender.path.name)
      //normalize the urls in the records

      val normalizedRecords = records.map { r => 
        URLUtil.normalize(baseUrl, r.newsUrl) match {
          case Success(normUrl) =>
            r.copy(newsUrl = normUrl)
          case Failure(_) =>
            r
        }
      }
      //remove the url contained in the stopUrls vector
      val filteredRecords = normalizedRecords.takeWhile(r => !stopUrls.contains(r.newsUrl))

      //start the main content extraction for each records
      filteredRecords.foreach { r =>
        val recordNews = News(None, Some(baseUrl), Some(r.newsUrl), Some(r.title), Some(r.summary), Some(r.newsDate))
        context.watch(context.actorOf(mainContentExtractorProps(recordNews), self.path.name + "-MainContent-" + countContentExtractors))
        countContentExtractors += 1
      }

      //go to the next status
      val nextIndex = if (filteredRecords.size < records.size)
        maxIndex + 1
      else
        currentIndex + 1

      // stay running waiting that all the main contents are extracted
      context.become(running(nextIndex, stopUrls, extractedUrls, jobSender, filteredRecords.size))

    case MainContentExtractor.Result(news) =>
      log.info("extracted news with title {} from {}",news.urlNews,sender.path.name)
      dbActor ! DBManager.InsertNews(news)
      
      //evaluate if we should go to run next
      
      //reduce the number of extractions
      context.become(running(currentIndex, stopUrls, extractedUrls :+ news.canonicalUrl.get, jobSender, runningExtractions-1))
    
    //this happens only for the MainContenExtractors
    case Terminated(ref) =>
      if (runningExtractions == 0){
        context.become(runNext(currentIndex, stopUrls, extractedUrls, jobSender))
      }
   
    case timeout: ReceiveTimeout =>
      log.error("Receiving timeout for News Extraction in {}", self.path.name)
      //allows the children to complete the current message evaluation
      context.children.foreach( _ ! PoisonPill)
      jobSender ! Fail(id,currentIndex,extractedUrls)
  }
}

