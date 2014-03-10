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
class BrindisiLiberaDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = "td.contentheading"

  def title(node: Element) = node.select("a").text

  def summary(node: Element) = node.select("a").text

  def newsUrl(node: Element) = node.select("a").attr("href")

}
