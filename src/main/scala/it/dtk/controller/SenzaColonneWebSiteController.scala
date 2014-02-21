package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class SenzaColonneWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 400
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.senzacolonnenews.it/cronaca.html?start="

  override def dataRecordExtractorProps: Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + ((v - 1) * 5), v))
  }
}
