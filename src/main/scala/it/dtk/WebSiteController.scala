package it.dtk

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.Terminated
import akka.actor.ReceiveTimeout
import it.dtk.db.{ DBManager, News }
import scala.util.{ Failure, Success }
import org.joda.time.DateTime
import it.dtk.db.DBManager
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import it.dtk.util.URLUtil

object WebSiteController {

  case class Start(stopUrl: Option[String] = None)

  case class Job(url: String, index: Int, running: Boolean = false)

  case class Done(url: String)

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

  // Max concurrent getter actors
  val parallelFactor = 3
  // Max call duration
  // context.setReceiveTimeout(10.seconds)

  val baseUrl: String
  val maxIncrement: Int

  protected val dbActor: ActorRef

  def dataRecordExtractorProps(url: String, html: String, date: DateTime): Props

  def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job]

  def httpGetterProps(url: String): Props = Props(classOf[HttpGetter], url)

  def mainContentExtractorProps(news: News): Props = Props(classOf[MainContentExtractor],news)
  
  def receive = waiting

  val waiting: Receive = {

    case Start(stopUrl) =>
      context.become(runBatch(stopUrl, parallelFactor))
  }

  def runBatch(stopUrl: Option[String], currentStop: Int): Receive = {

    //generate the jobs to the currentStop
    val jobs = logicalListUrlGenerator(1, currentStop)
    log.info("start processing incoming jobs")

    val runningJobs = jobs.map(job => {
      // Get HTML from each URL
      log.info("Getting the HTML for the page {} with index {}", job.url, job.index)
      context.watch(context.actorOf(httpGetterProps(job.url), "HttpGetter@" + job.index))
      job.copy(running = true)
    })

    running(stopUrl, currentStop)

  }

  def running(stopUrl: Option[String], currentStop: Int): Receive = {

    case Success(HttpGetter.Result(url, html, date)) =>
      log.info("Got the HTML for URL {} having size of {} bytes", url, html.size)
      context.watch(context.actorOf(dataRecordExtractorProps(url, html, new DateTime(date))))

    case Failure(HttpGetter.GetException(url, statusCode)) =>
      log.info("Failed to get the HTML for URL {} with status code {}", url, statusCode)

    case Failure(HttpGetter.DispatchException(url, error)) =>
      log.info("Failed to get the HTML for URL {} with exception message {}", url, error.getMessage)

    case DataRecordExtractor.DataRecords(url, date, records) =>
      //get the first n records which have url different from stopUrl
      //val toProcess = records.takeWhile(_.newsUrl != stopUrl.getOrElse(""))

      records.foreach(record => {
        // Extract main content from each data record
        log.info("Getting main article content for URL {}", record.newsUrl)
        val newsUrlNormalized = 
          if (URLUtil.isRelative(record.newsUrl)) 
            baseUrl + record.newsUrl else
              record.newsUrl
        val recordNews = News(None, Some(baseUrl), Some(newsUrlNormalized), Some(record.title), Some(record.summary), Some(date))
        context.watch(context.actorOf(mainContentExtractorProps(recordNews)))
      })

//      if (toProcess.size < records.size) {
//        //do not run the next batch because we found the stop url
//        context.become(running(stopUrl, maxIncrement))
//      }

    case MainContentExtractor.Result(news) =>
      log.info("Got main article content for URL {}", news.urlNews)
      dbActor ! DBManager.Insert(news)

    case DBManager.Fail(news) =>
      log.info("error inserting the new in the db {}", news.urlNews)

    case Terminated(ref) =>
      log.debug("Got a death letter from {}", ref)

      log.debug("number of children {}",context.children.size)
//      //get the new status. It can be runNextBatch of waiting
//      if ((context.children.isEmpty) && (currentStop < maxIncrement)) {
//        val newStart = currentStop + 1
//        val nextStop = if (newStart + parallelFactor < maxIncrement) newStart + parallelFactor else maxIncrement
//        context.become(runBatch(stopUrl, nextStop))
//      } else {
//        context.parent ! Done(baseUrl)
//        context.become(waiting)
//      }

    case ReceiveTimeout =>
      log.info("Failure in the extraction of the website {}", baseUrl)
      context.children foreach context.stop
      context.parent ! Fail(baseUrl)
  }

}

