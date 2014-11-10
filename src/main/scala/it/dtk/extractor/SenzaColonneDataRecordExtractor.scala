package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.Locale

class SenzaColonneDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {

  override val cssRecordsSelector: String = "div.itemContainer"

  def title(node: Element) = node.select("h3.catItemTitle").text()

  def summary(node: Element) = new String()

  def newsUrl(node: Element) = node.select("h3.catItemTitle > a").attr("href")

  def newsDate(node: Element, date: DateTime) = {
    val sdf = DateTimeFormat.forPattern("EEEE, dd MMMM yyyy HH:mm").withLocale(Locale.ITALIAN)
    sdf.parseDateTime(node.select("span[class=catItemDateCreated]").text().toLowerCase)
  }
}

