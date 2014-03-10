package it.dtk.extractor

import it.dtk.DataRecordExtractor
import it.dtk.DataRecordExtractor.DataRecord
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class LeccePrimaDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = "article[class= post p-small clearfix]"

  def title(node: Element) = node.select("header > figure > a > img[alt]").attr("alt")

  def summary(node: Element) = node.select("p").text

  def newsUrl(node: Element) = node.select("header > h1 > a").attr("href")
}
