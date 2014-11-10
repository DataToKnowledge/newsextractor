package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class Puglia24DataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "div.newsTxt"

  def title(node: Element) = node.select("h5 > a").text()

  def summary(node: Element) = node.select("p").text()

  def newsUrl(node: Element) = node.select("h5 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
