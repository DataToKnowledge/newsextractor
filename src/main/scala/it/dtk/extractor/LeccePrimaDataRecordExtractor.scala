package it.dtk.extractor

import it.dtk.DataRecordExtractor
import it.dtk.DataRecordExtractor.{DataRecord, DataRecords}
import java.util.Date
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._
import it.dtk.DataRecordExtractor._

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class LeccePrimaDataRecordExtractor(url: String, html: String, date: Date) extends DataRecordExtractor {
  
  implicit val doc = Jsoup.parse(html, url)

  //get the data records
  val records = dataRecordXPath("article[class= post p-small clearfix]") map (
    r => DataRecord(title(r), summary(r), newsUrl(r)))
    
  context.parent ! DataRecords(url,date,records)
    
  def title(node: Element) = node.select("header > figure > a > img[alt]").attr("alt")
  
  def summary(node: Element) = node.select("p").text
  
  def newsUrl(node: Element) = node.select("header > h1 > a").attr("href")
}
