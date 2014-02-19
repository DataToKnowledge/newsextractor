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

  var parallelFactor = 3

  val baseUrl: String

  val maxIncrement: Int

  def httpGetterProps(url: String): Props = Props(classOf[HttpGetter], url)

  def mainContentExtractorProps(record: DataRecord): Props = Props[MainContentExtractor]

  /**
   * this should be implemented in each class and point to the actual data record extractor
   */
  def dataRecordExtractorProps: Props

  def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job]

  //the maximum duration of the call
  //  context.setReceiveTimeout(10.seconds)

  //get the least record extracted from the db

  def receive = waiting

  val waiting: Receive = {

    case Start =>
      //start querying the first 
      val jobs = logicalListUrlGenerator(1, parallelFactor)
      context.become(runBatch(jobs, parallelFactor))
  }

  def runBatch(job: Seq[Job], currentEnd: Int): Receive = {
    if (job.isEmpty) {
      waiting
    } else {
      job.foreach(j => {
        log.info("Getting the HTML for the page {} with index {}", j.url, j.index)
        //start a http getter for each url and start getting the HTML
        context.watch(context.actorOf(httpGetterProps(j.url)))
      })

      running(job, currentEnd)
    }
  }

  def running(job: Seq[Job], currentEnd: Int): Receive = {
    //it misses the url and 
    case HttpGetter.Result(url, html, date) =>
      log.info("Getting the data records from the page {}", url)
      //FIXME check what happen if the http getter has an error
      context.watch(context.actorOf(dataRecordExtractorProps))

    case DataRecordExtractor.ExtractedRecords(url, records) =>
      records.foreach(record => {
        log.info("Getting the article main content from the page {}", record)
        context.watch(context.actorOf(mainContentExtractorProps(record)))
      })

    case res: MainContentExtractor.Result =>
      //save to the db
      println(res.record)

    case Terminated(_) =>
      if (context.children.isEmpty) {
        if (currentEnd < maxIncrement) {
          val start = currentEnd + 1
          val stop = if (start + parallelFactor < maxIncrement) start + parallelFactor else maxIncrement
          runBatch(logicalListUrlGenerator(start, stop), stop)
        } else {
          context.parent ! Done(baseUrl)
        }
      }

    case ReceiveTimeout =>
      context.children foreach context.stop
      log.info("Failure in the extraction of the website {}", baseUrl)
      context.parent ! Failure(baseUrl)
  }

}

