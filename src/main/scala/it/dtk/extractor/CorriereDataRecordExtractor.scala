package it.dtk.extractor

import it.dtk.DataRecordExtractor
import java.util.Date
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import org.jsoup.nodes.Element
import it.dtk.DataRecordExtractor._
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class CorriereDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = "li[class=clearfix]"

  def title(node: Element) = node.select("h2").text

  def summary(node: Element) = node.select("p").text

  def newsUrl(node: Element) = node.select("h2 > a").attr("href")

}
