package it.dtk.extractor

import it.dtk.DataRecordExtractor
import it.dtk.DataRecordExtractor.{ DataRecord, DataRecords }
import java.util.Date
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import org.jsoup.nodes.Element
import it.dtk.DataRecordExtractor._
import org.joda.time.DateTime

class RepubblicaDataRecordExtractor(url: String, html: String, date: DateTime)extends DataRecordExtractor {

  implicit val doc = Jsoup.parse(html, url)

  //get the data records
  val records = dataRecordXPath("article[class=article]") map (
    r => DataRecord(title(r), summary(r), newsUrl(r)))
    
  context.parent ! DataRecords(url,date,records)
    
  def title(node: Element) = node.select("h1").text
  
  def summary(node: Element) = node.select("p.summary").text
  
  def newsUrl(node: Element) = node.select("h1 > a").attr("href")

}