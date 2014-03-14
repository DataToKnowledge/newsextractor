package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiLiberaDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "td.contentheading"

  def title(node: Element) = node.select("a").text

  def summary(node: Element) = node.select("a").text

  def newsUrl(node: Element) = node.select("a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
