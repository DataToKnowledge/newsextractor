package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class CorriereDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "li[class=clearfix]"

  def title(node: Element) = node.select("h2").text

  def summary(node: Element) = node.select("p").text

  def newsUrl(node: Element) = node.select("h2 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
