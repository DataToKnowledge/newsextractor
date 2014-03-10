package it.dtk.extractor

import it.dtk.DataRecordExtractor
import scala.xml.{ XML, NodeSeq, Node }
import it.dtk.DataRecordExtractor.{ DataRecord, DataRecords }
import java.util.Date
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._
import org.joda.time.DateTime

class GiornaleDiPugliaDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = "post-wrapper"

  def title(node: Element) = node.getElementsByClass("post-title").text()

  def summary(node: Element) = node.getElementsByClass("post-body-snippet").text()

  def newsUrl(node: Element) = node.getElementsByClass("post-title").select("a").attr("href")
  def data(node: Element) = node.getElementsByClass("timestamp").text

}