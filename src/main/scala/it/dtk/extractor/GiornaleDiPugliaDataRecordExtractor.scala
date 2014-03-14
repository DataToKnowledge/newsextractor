package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import org.joda.time.DateTime
import akka.actor.ActorRef
import java.util.Locale
import org.joda.time.format.DateTimeFormat

class GiornaleDiPugliaDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "div[class=post-archive-body post-body]"

  def title(node: Element) = node.select("h2 > a").text()

  def summary(node: Element) = node.select("p").text()

  def newsUrl(node: Element) = node.select("h2 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = {
    val sdf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss a").withLocale(Locale.ENGLISH)
    sdf.parseDateTime(node.select("div > div[class=date]").text())
  }

}