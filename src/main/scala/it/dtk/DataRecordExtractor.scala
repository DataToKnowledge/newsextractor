package it.dtk

import akka.actor.Actor
import akka.actor.ActorLogging
import java.util.Date
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._
import org.joda.time.DateTime

object DataRecordExtractor {

  case class DataRecord(title: String, summary: String, newsUrl: String)
  case class DataRecords(url: String, date: DateTime, dataRecords: Seq[DataRecord])

}

trait DataRecordExtractor extends Actor with ActorLogging {
    
  def dataRecordXPath(cssSelector: String)(implicit doc: Document): Elements = doc.select(cssSelector)

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

  def receive: Actor.Receive = {
    case _ =>
  }

}