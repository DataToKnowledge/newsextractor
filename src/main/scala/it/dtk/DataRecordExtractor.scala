package it.dtk

import akka.actor.Actor
import akka.actor.ActorLogging
import scala.xml.Node
import scala.xml.NodeSeq
import scala.collection.mutable.Map
import it.dtk.DataRecordExtractor.DataRecords
import java.util.Date

object DataRecordExtractor {

  case class DataRecord(title: String, summary: String, newsUrl: String)

  case class DataRecords(url: String, date: Date, dataRecords: Seq[DataRecord])

}

trait DataRecordExtractor extends Actor with ActorLogging {

  def dataRecordXPath(e: Node): NodeSeq

  def title(e: Node): String

  def summary(e: Node): String

  def newsUrl(e: Node): String

  /**
   * @param node
   * @return a map with key url and list of text anchor as value
   */
  def linkExtractor(node: Node): Map[String, List[String]] = {

    val nodes = node \\ "a"
    val map = Map[String, List[String]]()

    for (n <- nodes) {
      val text = n.text
      val nodes = n.attribute("href")
      //add the href only if it exists and it is not empty
      nodes.map(n => n.text) match {
        case Some(href) if !href.isEmpty =>
          if (!map.contains(href))
            map += href -> List()

          map += href -> map(href) .:: (text)
        case _ =>
      }
    }
    map
  }

  override def receive: Actor.Receive = ???

}