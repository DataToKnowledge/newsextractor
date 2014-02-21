package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class QuotidianoDiPugliaWebSiteController extends WebSiteController {

  override val maxIncrement: Int = 1

  override val baseUrl: String = "http://www.quotidianodipuglia.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: Date): Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "leggitutte.php?sez=ATTUALITA", v))
  }
}
