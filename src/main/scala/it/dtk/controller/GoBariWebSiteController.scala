package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class GoBariWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 162
  override val maxIncrement: Int = 5

  // http://go-bari.it/notizie/cronaca/
  override val baseUrl: String = "http://go-bari.it/"

  override def dataRecordExtractorProps: Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "index.php?id=0|2&idS=19&pageID=" + v, v))
  }
}
