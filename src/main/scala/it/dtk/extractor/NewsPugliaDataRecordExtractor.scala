package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class NewsPugliaDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "div.contentpaneopen"

  def title(node: Element) = node.select("h2 > a").text

  def summary(node: Element) = node.select("div.faf-text > p").text

  def newsUrl(node: Element) = node.select("h2 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
