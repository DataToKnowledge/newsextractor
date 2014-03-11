package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class CorriereSalentinoDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = "#posts-list > div.right"

  def title(node: Element) = node.select("h2 > a").text

  def summary(node: Element) = node.select("div.post > p").text

  def newsUrl(node: Element) = node.select("h2 > a").attr("href")

}
