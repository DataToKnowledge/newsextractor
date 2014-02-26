package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import org.joda.time.DateTime
import java.util.Locale
import java.util.Date

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class CorriereWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 294
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.corriere.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: Date): Props = ???//Props(classOf[],url,html,date)

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    val date = DateTime.now()

    start to stop map (v => Job(baseUrl + "cronache/notizie/" + date.monthOfYear().getAsText(Locale.ITALIAN) +
      '_' + date.year().getAsText + '_' + v + ".html", v))
  }
}
