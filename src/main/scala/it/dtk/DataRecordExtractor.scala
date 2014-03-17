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

  /**
   * @param node
   * @return a map with key url and list of text anchor as value
   */
  def linkExtractor(node: Element): Map[String, List[String]] = {
    //get the nodes
    val nodes = node.select("a[href]")

    var map: Map[String, List[String]] = Map()

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
      log.debug("Got the HTML for URL {} having size of {} bytes", url, html.size)

      val doc = Jsoup.parse(html)
      //get the data records
      val records = extractRecords(doc) map (
        r => DataRecord(title(r), summary(r), newsUrl(r), newsDate(r, date)))

      context.parent ! DataRecords(url, records.filter(d => d.title.nonEmpty))

    case HttpGetter.Fail(url, ex) =>
      log.error("Failed to get the HTML for URL {} with exception {}", url, ex.getMessage)
  }
}