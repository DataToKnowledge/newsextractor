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
class GoBariDataRecordExtractor(url: String, html: String, date: DateTime) extends DataRecordExtractor {

  implicit val doc = Jsoup.parse(html, url)

  //get the data records
  val records = dataRecordXPath("div.centro_sx_home_news") map (
    r => DataRecord(title(r), summary(r), newsUrl(r))) filter(_.title.length() > 0)
  
    
  context.parent ! DataRecords(url,date,records)
    
  def title(node: Element) = node.select("div.titolo > a").text
  
  def summary(node: Element) = node.select("span.civetta > p").text
  
  def newsUrl(node: Element) = node.select("div.titolo > a").attr("href")

}
