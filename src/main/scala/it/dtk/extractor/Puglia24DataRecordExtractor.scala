package it.dtk.extractor

import it.dtk.DataRecordExtractor
import scala.xml.{ XML, NodeSeq, Node }
import it.dtk.DataRecordExtractor.{ DataRecord, DataRecords }
import java.util.Date
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import org.jsoup.nodes.Element
import it.dtk.DataRecordExtractor._
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class Puglia24DataRecordExtractor(url: String, html: String, date: DateTime) extends DataRecordExtractor {

  override val cssRecordsSelector: String = "div[class= grid_7]"

  def title(node: Element) = node.select("a > h2").text

  def summary(node: Element) = node.select("div > p:last-child").text

  def newsUrl(node: Element) = node.select("a[href]").attr("href")

}
