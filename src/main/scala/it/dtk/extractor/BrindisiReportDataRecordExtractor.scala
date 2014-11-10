package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiReportDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "article.post"

  def title(node: Element) =
    node.select("header > figure > a > img[alt]").attr("alt")

  def summary(node: Element) =
    node.select("p").text

  def newsUrl(node: Element) =
    node.select("header > h1 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
