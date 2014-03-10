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
class NewsPugliaDataRecordExtractor(url: String, html: String, date: DateTime) extends DataRecordExtractor {

  override val cssRecordsSelector: String = "ul[class~=leadingblock|introblock] > li"

  def title(node: Element) = node.select("div > h2 > a").text

  def summary(node: Element) = node.select("div > div[class= lineinfo line1").text

  def newsUrl(node: Element) = node.select("div > h2 > a[href]").attr("href")

}
