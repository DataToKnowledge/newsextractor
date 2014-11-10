package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class QuotidianoDiPugliaDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "article.col-12"

  def title(node: Element) = node.select("p.titolo > a").text

  def summary(node: Element) = node.select("p.sottotitolo").text

  def newsUrl(node: Element) = node.select("p.titolo > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
