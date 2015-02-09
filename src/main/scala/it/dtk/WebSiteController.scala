package it.dtk

import akka.actor.SupervisorStrategy._
import akka.actor.{ Actor, ActorLogging, ActorRef, OneForOneStrategy, PoisonPill, Props, ReceiveTimeout, actorRef2Scala }
import akka.contrib.throttle.Throttler.{ SetTarget, Rate }
import akka.contrib.throttle.TimerBasedThrottler
import it.dtk.DataModel._

import scala.concurrent.duration._

object WebSiteController {

  case class Start(lastUrl: Option[String], currentIndex: Int = 1)

  case class Job(url: String, index: Int, running: Boolean = false)

  case class Done(nameController: String, extractedUrls: Vector[String])

  case class Fail(nameController: String, currentIndex: Int, extractedUrls: Vector[String])

  case object Status

  case object Waiting

  case object Running

}

/**
 * @author Fabio
 *
 */
abstract class WebSiteController(val name: String) extends Actor with ActorLogging {

  import it.dtk.WebSiteController._

  val baseUrl: String
  val maxIndex: Int
  val call: Int

  def composeUrl(currentIndex: Int): String
  def dataRecordExtractorProps(http: ActorRef): Props

  val httpRouter = context.actorOf(HttpActor.props, self.path.name + "-HttpRouter")
  //val throttler = context.actorOf(Props(classOf[TimerBasedThrottler], new Rate(call, 1.second)))
  //throttler ! SetTarget(Some(httpRouter))

  val drActor = context.actorOf(dataRecordExtractorProps(httpRouter), self.path.name + "-DataRecord")

  val dbActor = context.actorOf(MongoDbActor.props())

  var countContentExtractors = 0

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 100.seconds) {
    case ex: Exception =>
      log.error("actor {} throw {} ", sender.path.name, ex.getMessage())
      Restart
  }

  def receive = waiting

  val waiting: Receive = {

    case Start(stopUrls, currentIndex) =>
      log.info("start processing {}", self.path.name)
      context.become(runNext(currentIndex, stopUrls, Vector[String](), sender))

    case Status =>
      Waiting
  }

  def runNext(currentIndex: Int, stopUrl: Option[String], extractedUrls: Vector[String], jobSender: ActorRef): Receive = {
    if (currentIndex > maxIndex) {
      log.info("End Processing {}", self.path.name)
      jobSender ! Done(name, extractedUrls)
      waiting
    }
    else {
      log.info("Processing Page {} for the Actor {}", currentIndex, self.path.name)
      val nextUrl = composeUrl(currentIndex)
      drActor ! DataRecordExtractor.Extract(nextUrl)
      running(currentIndex, stopUrl, extractedUrls, jobSender, 0)
    }
  }

  def running(currentIndex: Int, stopUrl: Option[String], extractedUrls: Vector[String], jobSender: ActorRef, runningExtractions: Int): Receive = {

    case Status =>
      sender ! Running

    case DataRecordExtractor.DataRecords(url, records) =>
      log.info("from page {} got {} data records from {}", url, records.size, sender.path.name)
      //normalize the urls in the records
      import it.dtk.util.URLUtil._
      val normalizedRecords = records.map { r =>
        r.copy(
          newsUrl = normalizeOpt(baseUrl, r.newsUrl).getOrElse(r.newsUrl))
      }

      //remove the url contained in the stopUrls vector
      val filteredRecords = normalizedRecords.takeWhile(r => !stopUrl.contains(r.newsUrl))

      extractMainContent(filteredRecords)

      val nextStatus =
        if (filteredRecords.size == 0)
          runNext(maxIndex + 1, stopUrl, extractedUrls, jobSender)
        else if (filteredRecords.size < records.size)
          running(maxIndex + 1, stopUrl, extractedUrls, jobSender, filteredRecords.size)
        else
          running(currentIndex + 1, stopUrl, extractedUrls, jobSender, filteredRecords.size)

      // stay running waiting that all the main contents are extracted
      context.become(nextStatus)

    case DataRecordExtractor.Fail(url, code) =>
      log.error("Failure getting {} for {}", url, sender.path.name)
      context.become(runNext(currentIndex + 1, stopUrl, extractedUrls, jobSender))

    case MainContentExtractor.Result(news) =>
      log.info("saving news with title {} from {}", news.urlNews, sender.path.name)
      if (!news.corpus.isEmpty)
        dbActor ! MongoDbActor.Save(news)

      //evaluate if we should go to run next
      if (runningExtractions == 1)
        context.become(runNext(currentIndex, stopUrl, extractedUrls, jobSender))
      else
        //reduce the number of extractions
        context.become(running(currentIndex,
          stopUrl, extractedUrls :+ news.canonicalUrl, jobSender, runningExtractions - 1))

    case MainContentExtractor.FailContent(url, ex) =>
      log.error("fail extracting url {} with exception {}", url, ex.toString())
      if (runningExtractions == 1)
        context.become(runNext(currentIndex, stopUrl, extractedUrls, jobSender))
      else
        //reduce the number of extractions
        context.become(running(currentIndex, stopUrl, extractedUrls, jobSender, runningExtractions - 1))

    case timeout: ReceiveTimeout =>
      log.error("Receiving timeout for News Extraction in {}", self.path.name)
      //allows the children to complete the current message evaluation
      context.children.foreach(_ ! PoisonPill)
      jobSender ! Fail(name, currentIndex, extractedUrls)

    case akka.actor.Status.Failure(ex) =>
      log.error("Receiving error from future pipe {}", ex)

  }

  def extractMainContent(records: Seq[DataRecord]) = {
    val baseTime = 2
    var i = 1
    //start the main content extraction for each records
    records.foreach { r =>

      //send delayed messages
      val nexTime = baseTime * i

      val name = self.path.name + "-MainContent-" + countContentExtractors

      val mainContentActor = context.actorOf(MainContentExtractor.props(baseUrl, r, httpRouter),name)

      i += 1
      countContentExtractors += 1
    }
  }

}

