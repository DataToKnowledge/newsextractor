package it.dtk

import akka.actor.Actor
import akka.actor.ActorLogging
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._
import org.joda.time.DateTime
import akka.actor.Props
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import scala.util.Success
import org.jsoup.Jsoup
import scala.util.Failure
import akka.actor.ActorRef

object DataRecordExtractor {
  case class Extract(url: String)
  case class DataRecord(title: String, summary: String, newsUrl: String)
  case class DataRecords(url: String, date: DateTime, dataRecords: Seq[DataRecord])

}

abstract class DataRecordExtractor(val routerHttpGetter: ActorRef) extends Actor with ActorLogging {

  import DataRecordExtractor._

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = -1, loggingEnabled = true) {

    case e: Exception =>
      log.error("got exception in DataRecordExtractor {}",e.getMessage())
      SupervisorStrategy.Restart

    case e: Throwable =>
      log.error("got exception in DataRecordExtractor {}",e.getMessage())
      SupervisorStrategy.Restart
  }

  val cssRecordsSelector: String

  def extractRecords(doc: Document): Elements =
    doc.select(cssRecordsSelector)

  def title(node: Element): String

  def summary(node: Element): String

  def newsUrl(node: Element): String

  /**
   * @param node
   * @return a map with key url and list of text anchor as value
   */
  def linkExtractor(node: Element): Map[String, List[String]] = {
    //get the nodes
    val nodes = node.select("a[href]")

    var map = Map[String, List[String]]()

    for (n <- nodes) {
      val text = n.text
      val href: String = n.attr("href")

      val list = map.get(href) match {
        case Some(l) => text :: l
        case None => List(text)
      }
      map + (href -> list)
    }
    map.toMap
  }

  def receive = {

    case Extract(url) =>
      routerHttpGetter ! HttpGetter.Get(url)

    case HttpGetter.Result(url, html, date) =>
      log.info("Got the HTML for URL {} having size of {} bytes", url, html.size)

      val doc = Jsoup.parse(html)
      //get the data records
      val records = extractRecords(doc) map (
        r => DataRecord(title(r), summary(r), newsUrl(r)))

      context.parent ! DataRecords(url, new DateTime(date), records.filter(d => d.title.length() > 0))

    case HttpGetter.Fail(url, ex) =>
      log.error("Failed to get the HTML for URL {} with exception {}", url, ex.getMessage())

  }
}