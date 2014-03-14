package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class QuotidianoDiPugliaDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String =
    "html > body > table > tbody > tr > td > table[bgcolor=#ffffff] > tbody > tr > td[valign=top] > table"

  def title(node: Element) = node.select("table > tbody > tr > td > a.nero18").text

  def summary(node: Element) = node.select("table > tbody > tr > td > div > span.testonero12").text

  def newsUrl(node: Element) = node.select("table > tbody > tr > td > a.nero18").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}
