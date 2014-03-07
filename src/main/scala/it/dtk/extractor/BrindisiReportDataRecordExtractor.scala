package it.dtk.extractor

import it.dtk.DataRecordExtractor
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
class BrindisiReportDataRecordExtractor(url: String, html: String, date: DateTime) extends DataRecordExtractor {

  implicit val doc = Jsoup.parse(html, url)

  //get the data records
  val records = dataRecordXPath("div.postm > div.postm") map (
    r => DataRecord(title(r), summary(r), newsUrl(r)))
    
  context.parent ! DataRecords(url,date,records)
    
  def title(node: Element) = node.select("div.ptitle > a").text
  
  def summary(node: Element) = node.select("div.entry > p").text
  
  def newsUrl(node: Element) = node.select("div.ptitle > a").attr("href")

}
