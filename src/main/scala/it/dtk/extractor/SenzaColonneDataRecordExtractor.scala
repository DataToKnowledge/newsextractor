package it.dtk.extractor

import it.dtk.DataRecordExtractor
import scala.xml.{XML, NodeSeq, Node}
import it.dtk.DataRecordExtractor.{DataRecord, DataRecords}
import java.util.Date
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._
import org.joda.time.DateTime

class SenzaColonneDataRecordExtractor(url: String, html: String, date: DateTime) extends DataRecordExtractor {

  implicit val doc = Jsoup.parse(html, url)

  //get the data records
  val records = dataRecordXPath("div.itemContainer") map (
    r => DataRecord(title(r), summary(r), newsUrl(r)))
    
  context.parent ! DataRecords(url,date,records)
    
  def title(node: Element) = node.getElementsByClass("catItemTitle").text()
  
  def summary(node: Element) = null
  
  def newsUrl(node: Element) = node.getElementsByClass("catItemTitle").select("a").attr("href")

  def data(node: Element) = node.getElementsByClass("catItemDateCreated").text
}

