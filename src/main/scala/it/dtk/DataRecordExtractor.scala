package it.dtk

import akka.actor.Actor
import akka.actor.ActorLogging
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._
import org.joda.time.DateTime
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import org.jsoup.Jsoup
import akka.actor.ActorRef

object DataRecordExtractor {
  case class Extract(url: String)
  case class DataRecord(title: String, summary: String, newsUrl: String, newsDate: DateTime)
  case class DataRecords(url: String, dataRecords: Seq[DataRecord])
  case class Fail(url: String, code: Option[Int])
  private[DataRecordExtractor] case class Retry(url: String)

}

abstract class DataRecordExtractor(val routerHttpGetter: ActorRef) extends Actor with ActorLogging {

  import DataRecordExtractor._

  val cssRecordsSelector: String

  def extractRecords(doc: Document): Elements =
    doc.select(cssRecordsSelector)

  def title(node: Element): String

  def summary(node: Element): String

  def newsUrl(node: Element): String

  def newsDate(node: Element, date: DateTime): DateTime
  
  private var retryMap: Map[String,Int] = Map[String,Int]()
  private val maxRetries = 5
  
  
  def receive = {

    case Extract(url) =>
      retryMap+= url -> 0
      routerHttpGetter ! HttpGetter.Get(url)

    case HttpGetter.Result(url, html, date) =>
      log.debug("Got the HTML for URL {} having size of {} bytes", url, html.size)

      val doc = Jsoup.parse(html)
      //get the data records
      val records = extractRecords(doc) map (
        r => DataRecord(title(r), summary(r), newsUrl(r), newsDate(r, date)))

      context.parent ! DataRecords(url, records.filter(d => d.title.nonEmpty))

    case HttpGetter.Fail(url, ex) =>
      ex match {
        case BadStatus(url, code) =>
          log.error("Failed to get HTML from {} with status code {} from {}", url,code, sender.path.name)
          context.parent ! Fail(url,Option(code))
          
        case GetException(url,ex) =>
          log.error("Retrying Failed to get HTML from {} with exception {}",url, ex.getStackTraceString)
          //retry after timeout
          self ! Retry(url)
      }
      
    case Retry(url) =>
      retryMap.get(url).map { times =>
      	if (times < maxRetries){
      	  retryMap += url -> (times + 1)
      	  self ! Extract(url)
      	}else{
      	  retryMap -=url
      	  log.error("Failed to get HTML from {} after {} tries",url, maxRetries)
      	  context.parent ! Fail(url,None)
      	}
      }
  }
}