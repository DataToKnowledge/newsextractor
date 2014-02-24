package it.dtk

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.Terminated
import akka.actor.ReceiveTimeout
import it.dtk.db.News
import scala.util.{Failure, Success}
import java.util.Date

object WebSiteController {

  case object Start

  case class Job(url: String, index: Int, terminated: Boolean = false)

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

  def dataRecordExtractorProps(url: String, html: String, date: Date): Props

  def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job]

  def httpGetterProps(url: String): Props = Props(classOf[HttpGetter], url)

  def mainContentExtractorProps(record: News): Props = Props[MainContentExtractor]

  private var activeJobs = 0
  private var currentStop = -1

  def receive = waiting

  val waiting: Receive = {
    case Start =>
      val jobs = logicalListUrlGenerator(1, parallelFactor)
      currentStop = parallelFactor
      context.become(runBatch(jobs))
  }

  def runBatch(jobs: Seq[Job]): Receive = {
    if (jobs.isEmpty) {
      log.info("All done. Waiting for new jobs.")
      currentStop = -1
      waiting
    } else {
      log.info("Launching getter cycle from index {} to {}", jobs.head.index, jobs.last.index)
      jobs.foreach(job => {
        // Get HTML from each URL
        log.info("Getting the HTML for the page {} with index {}", job.url, job.index)
        context.watch(context.actorOf(httpGetterProps(job.url), "HttpGetter@" + job.index))
        activeJobs += 1
      })
      running
    }
  }

  def running: Receive = {

    case Success(HttpGetter.Result(url, html, date)) =>
      log.info("Got the HTML for URL {} having size of {} bytes", url, html.size)
      context.watch(context.actorOf(dataRecordExtractorProps(url, html, date)))

    case Failure(HttpGetter.GetException(url, statusCode)) =>
      log.info("Failed to get the HTML for URL {} with status code {}", url, statusCode)
      // FIXME: Check what happens if the HttpGetter has an error

    case Failure(HttpGetter.DispatchException(url, error)) =>
      log.info("Failed to get the HTML for URL {} with exception message {}", url, error.getMessage)
      // FIXME: Check what happens if the HttpGetter has an error

    case DataRecordExtractor.DataRecords(url, date, records) =>
      records.foreach(record => {
        // Extract main content from each data record
        log.info("Getting main article content for URL {}", url)

        val recordNews = new News(0, baseUrl, record.newsUrl, record.title, record.summary, date)

        context.watch(context.actorOf(mainContentExtractorProps(recordNews)))
      })

    case MainContentExtractor.Result(news) =>
        // TODO: DB persistency
        log.info("Got main article content for URL {}", news.urlNews)

    case Terminated(ref) =>
      log.debug("Got a death letter from {}", ref)
      activeJobs -= 1

      if (activeJobs == 0) {
        if (currentStop < maxIncrement) {
          val newStart = currentStop + 1
          currentStop = if (newStart + parallelFactor < maxIncrement) newStart + parallelFactor else maxIncrement
          runBatch(logicalListUrlGenerator(newStart, currentStop))
        } else {
          context.parent ! Done(baseUrl)
          runBatch(Seq.empty)
        }
      }

    case ReceiveTimeout =>
      log.info("Failure in the extraction of the website {}", baseUrl)
      context.children foreach context.stop
      context.parent ! Fail(baseUrl)
  }

}

