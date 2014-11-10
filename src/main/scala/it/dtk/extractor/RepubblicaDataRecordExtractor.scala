package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime

class RepubblicaDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "article.article"

  def title(node: Element) = node.select("h1").text

  def summary(node: Element) = node.select("p.summary").text

  def newsUrl(node: Element) = node.select("h1 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = date

}