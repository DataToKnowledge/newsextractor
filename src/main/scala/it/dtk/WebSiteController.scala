package it.dtk

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import scala.concurrent.duration._
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorLogging
import it.dtk.http.HttpGetter
import akka.actor.Terminated
import akka.actor.ReceiveTimeout

object WebSiteController {
  case object Start
  case class Job(url: String, index: Int, terminated: Boolean = false)
  case class Done(url: String)
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

  /**
   * this should be implemented in each class and point to the actual data record extractor
   */
  def dataRecordExtractorProps: Props

  def httpGetterProps(url: String): Props

  def mainContentExtractorProps(url: String, record: String): Props

  def computeJobs(url: String, start: Int, end: Int) = start to end map (v => Job(url + v, v))

  def numParallelExtractions = 3

  private val baseUrl = "http://bari.repubblica.it/cronaca/"

  private val maxIncrement = 23

  //the maximum duration of the call
  //  context.setReceiveTimeout(10.seconds)

  //get the least record extracted from the db

  def receive = waiting

  val waiting: Receive = {

    case Start =>
      //generate the jobs with the urls
      val end = numParallelExtractions
      val jobs = computeJobs(baseUrl, 1, end)
      context.become(runBatch(jobs, end))
  }

  def runBatch(job: Seq[Job], end: Int): Receive = {
    if (job.isEmpty) waiting
    else {
      job.foreach(j => {
        log.debug("Getting the HTML for the page {} with index {}", j.url, j.index)
        //start a http getter for each url and start getting the HTML
        context.watch(context.actorOf(httpGetterProps(j.url)))
      })
      running(job, end)
    }
  }

  def running(job: Seq[Job], end: Int): Receive = {
    //it misses the url and 
    case HttpGetter.Result(html, date) =>
      val url = "" //FIXME 
      log.debug("Getting the data records from the page {}", url)
      //FIXME check what happen if the http getter has an error
      context.watch(context.actorOf(dataRecordExtractorProps))

    case DataRecordExtractor.ExtractedRecords(url, records) =>
      records.foreach(r => {
        log.debug("Getting the article main content from the page {}", r)
        context.watch(context.actorOf(mainContentExtractorProps(url, r)))
      })

    case res: MainContentExtractor.Result =>
    //save to the db

    case Terminated(_) => {
      if (context.children.isEmpty)
        if (end < maxIncrement) {
          val start = end + 1
          val last = if (start + numParallelExtractions < maxIncrement) start + numParallelExtractions else maxIncrement
          runBatch(computeJobs(baseUrl, start, last), last)
        } else {
          context.parent ! Done(baseUrl)
        }
    }

    case ReceiveTimeout =>
      context.children foreach context.stop

  }

}

