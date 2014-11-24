package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class GoBariDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "div.centro_sx_home_news > div.block"

  def title(node: Element) = node.select("div.titolo > a").text

  def summary(node: Element) = node.select("span.civetta").text

  def newsUrl(node: Element) = node.select("div.titolo > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
