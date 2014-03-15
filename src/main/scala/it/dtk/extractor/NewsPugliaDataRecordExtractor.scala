package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class NewsPugliaDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "ul[class~=leadingblock|introblock] > li"

  def title(node: Element) = node.select("div > h2 > a").text

  def summary(node: Element) = node.select("div > div[class=lineinfo line1]").text

  def newsUrl(node: Element) = node.select("div > h2 > a[href]").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
