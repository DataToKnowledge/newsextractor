package it.dtk.extractor

import it.dtk.DataRecordExtractor
import it.dtk.DataRecordExtractor.DataRecord
import scala.collection.JavaConversions._
import org.jsoup.nodes.Element
import it.dtk.DataRecordExtractor._

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiReportDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = "div.postm > div.postm"

  def title(node: Element) = node.select("div.ptitle > a").text

  def summary(node: Element) = node.select("div.entry > p").text

  def newsUrl(node: Element) = node.select("div.ptitle > a").attr("href")

}
