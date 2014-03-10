package it.dtk.extractor

import it.dtk.DataRecordExtractor
import it.dtk.DataRecordExtractor.{ DataRecord, DataRecords }
import java.util.Date
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import org.jsoup.nodes.Element
import it.dtk.DataRecordExtractor._
import org.joda.time.DateTime

class RepubblicaDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = 
    "article[class=article]"

  def title(node: Element) = node.select("h1").text

  def summary(node: Element) = node.select("p.summary").text

  def newsUrl(node: Element) = node.select("h1 > a").attr("href")

}