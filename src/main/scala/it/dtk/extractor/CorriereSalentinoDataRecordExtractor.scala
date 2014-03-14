package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class CorriereSalentinoDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "#posts-list > div.right"

  def title(node: Element) = node.select("h2 > a").text

  def summary(node: Element) = node.select("div.post > p").text

  def newsUrl(node: Element) = node.select("h2 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
