package it.dtk.extractor

import it.dtk.DataRecordExtractor
import org.jsoup.nodes.Element
import akka.actor.ActorRef
import org.joda.time.DateTime
import java.util.Locale
import org.joda.time.format.DateTimeFormat

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class PoliziaDiStatoDataRecordExtractor(routerHttpGetter: ActorRef) extends DataRecordExtractor(routerHttpGetter) {
  
  override val cssRecordsSelector: String = "div.notizia"

  def title(node: Element) =
    node.select("h3").text

  def summary(node: Element) =
    node.select("div.content").text

  def newsUrl(node: Element) =
    node.select("h3 > a").attr("href")

  def newsDate(node: Element, date: DateTime) = {
    val sdf = DateTimeFormat.forPattern("dd-MM-yyyy").withLocale(Locale.ITALIAN)
    sdf.parseDateTime(node.select("div.content > strong > em").text())
  }
}
