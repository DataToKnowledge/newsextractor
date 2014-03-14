package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import org.joda.time.DateTime
import akka.actor.ActorRef
import java.util.Locale
import org.joda.time.format.DateTimeFormat

class GiornaleDiPugliaDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "div[class=item-other]"

  def title(node: Element) = node.select("h3 > a").text()

  def summary(node: Element) = new String()

  def newsUrl(node: Element) = node.select("h3 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = {
    val sdf = DateTimeFormat.forPattern("dd-MM-yyyy").withLocale(Locale.ITALIAN)
    sdf.parseDateTime(node.select("div > a[class=date]").text())
  }

}