package it.dtk

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.Props
import akka.actor.ActorLogging
import akka.actor.Terminated
import akka.actor.ReceiveTimeout
import it.dtk.db.DataRecord

object WebSiteController {

  case object Start
  case class Job(url: String, index: Int, terminated: Boolean = false)
  case class Done(url: String)
  case class Failure(url: String)

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
  var parallelFactor = 3
  // Max call duration
  // context.setReceiveTimeout(10.seconds)

  val baseUrl: String
  val maxIncrement: Int

  def dataRecordExtractorProps: Props

  def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job]

  def httpGetterProps(url: String): Props = Props(classOf[HttpGetter], url)

  def mainContentExtractorProps(record: DataRecord): Props = Props[MainContentExtractor]

  def receive = waiting

  val waiting: Receive = {
    case Start =>
      val jobs = logicalListUrlGenerator(1, parallelFactor)
      context.become(runBatch(jobs, parallelFactor))
  }

  def runBatch(jobs: Seq[Job], currentStop: Int): Receive = {
    if (jobs.isEmpty) {
      waiting
    } else {
      jobs.foreach(job => {
        // Get HTML from each URL
        log.info("Getting the HTML for the page {} with index {}", job.url, job.index)
        context.watch(context.actorOf(httpGetterProps(job.url)))
      })

      running(jobs, currentStop)
    }
  }

  def running(job: Seq[Job], currentStop: Int): Receive = {
    case HttpGetter.Result(url, html, date) =>
      // Extract data records from each HTML
      log.info("Getting the data records from the page {}", url)
      // FIXME: Check what happens if the HttpGetter has an error
      context.watch(context.actorOf(dataRecordExtractorProps))

    case DataRecordExtractor.ExtractedRecords(url, records) =>
      records.foreach(record => {
        // Extract main content from each data record
        log.info("Getting the article main content from the page {}", record)
        context.watch(context.actorOf(mainContentExtractorProps(record)))
      })

    case res: MainContentExtractor.Result =>
      // TODO: DB persistency
      println(res.record)

    case Terminated(_) =>
      if (context.children.isEmpty) {
        // Launch next HttpGetter actors cycle
        if (currentStop < maxIncrement) {
          val newStart = currentStop + 1
          val newStop = if (newStart + parallelFactor < maxIncrement) newStart + parallelFactor else maxIncrement
          runBatch(logicalListUrlGenerator(newStart, newStop), newStop)
        } else {
          context.parent ! Done(baseUrl)
        }
      }

    case ReceiveTimeout =>
      log.info("Failure in the extraction of the website {}", baseUrl)
      context.children foreach context.stop
      context.parent ! Failure(baseUrl)
  }

}

