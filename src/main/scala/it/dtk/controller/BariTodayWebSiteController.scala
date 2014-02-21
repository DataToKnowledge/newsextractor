package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BariTodayWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 294
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.baritoday.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: Date): Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "cronaca/pag/" + v, v))
  }
}
