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
class QuotidianoDiPugliaDataRecordExtractor(url: String, html: String, date: DateTime) extends DataRecordExtractor {

  implicit val doc = Jsoup.parse(html, url)

  //get the data records
  val records = dataRecordXPath("html > body > table > tbody > tr > td > table[bgcolor=#ffffff] > tbody > tr > td[valign=top] > table").map(r => DataRecord(title(r), summary(r), newsUrl(r)))
    
  
  context.parent ! DataRecords(url,date,records.filter(d => d.title.length() > 0))
    
  def title(node: Element) = node.select("table > tbody > tr > td > a.nero18").text
  
  def summary(node: Element) = node.select("table > tbody > tr > td > div > span.testonero12").text
  
  def newsUrl(node: Element) = node.select("table > tbody > tr > td > a.nero18").attr("href")

}
