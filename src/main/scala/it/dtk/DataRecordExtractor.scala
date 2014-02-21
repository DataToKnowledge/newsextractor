package it.dtk

import akka.actor.Actor
import it.dtk.db.DataRecord
import akka.actor.ActorLogging
import scala.xml.Elem
import scala.xml.NodeSeq
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

object DataRecordExtractor {

  case class ExtractedRecords(url: String, dataRecords: ListBuffer[DataRecord])

}

trait DataRecordExtractor extends Actor with ActorLogging {

  def dataRecordXPath(e: Elem): NodeSeq

  def title(e: Elem): String

  def summary(e: Elem): String

  def newsDetailUrl(e: Elem): String

  /**
   * @param node
   * @return a map with key url and list of text anchor as value
   */
  def linkExtractor(node: Elem): Map[String, List[String]] = {

    val nodes = node \\ "a"
    val map = Map[String, List[String]]()

    for (n <- nodes) {
      val text = n.text
      val nodes = n.attribute("href")
      //add the href only if it exists and it is not empty
      nodes.map(n => n.text) match {
        case Some(href) if href.length > 0 => {
          val list = if (map.contains(href)) map(href) else List()
          map += href -> (text :: list)
        }
        case _ =>
      }
    }
    map
  }

  override def receive: Actor.Receive = ???

}