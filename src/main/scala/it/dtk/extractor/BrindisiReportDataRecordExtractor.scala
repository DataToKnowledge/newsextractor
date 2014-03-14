package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiReportDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "div.postm > div.postm"

  def title(node: Element) = node.select("div.ptitle > a").text

  def summary(node: Element) = node.select("div.entry > p").text

  def newsUrl(node: Element) = node.select("div.ptitle > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
