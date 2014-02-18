package it.dtk

import akka.actor.Actor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import scala.concurrent.duration._
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorLogging
import it.dtk.http.HttpGetter

object WebSiteController {
  case object Start
  private case class Job(url: String, index: Int, terminated: Boolean = false)
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
      val jobs = 1 to 3 map (v => Job(baseUrl + v, v))
      context.become(runBatch(jobs))
  }

  def runBatch(job: Seq[Job]): Receive = {

    if (job.isEmpty) waiting
    else {
      job.foreach(j => {
        log.debug("Getting the HTML for the page {} with index {}", j.url, j.index)
        //start a http getter for each url and start getting the HTML
        context.watch(context.actorOf(httpGetterProps(j.url)))
      })
      running(job)
    }

  }

  def running(job: Seq[Job]): Receive = {
    case HttpGetter.Result(html,date) => 
      //call the actor to retrieve t
  }

}

