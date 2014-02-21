package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiReportWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 717
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.brindisireport.it/cronaca/page/"

  override def dataRecordExtractorProps: Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + v, v))
  }
}