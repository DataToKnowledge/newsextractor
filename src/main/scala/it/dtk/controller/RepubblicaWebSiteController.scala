package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class RepubblicaWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 12
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://bari.repubblica.it/cronaca/"

  override def dataRecordExtractorProps: Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + v, v))
  }
}
