package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class Puglia24NewsWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 133
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.puglia24news.it/"

  override def dataRecordExtractorProps: Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "category/cronaca/page/" + v, v))
  }
}
