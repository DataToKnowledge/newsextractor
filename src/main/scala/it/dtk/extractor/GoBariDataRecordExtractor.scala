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
class GoBariDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = "div.centro_sx_home_news"

  def title(node: Element) = node.select("div.titolo > a").text

  def summary(node: Element) = node.select("span.civetta > p").text

  def newsUrl(node: Element) = node.select("div.titolo > a").attr("href")

}
