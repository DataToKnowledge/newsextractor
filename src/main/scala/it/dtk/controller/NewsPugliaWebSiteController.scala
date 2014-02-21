package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class NewsPugliaWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 285
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.newspuglia.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: Date): Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "index.php?option=com_flexicontent&view=category&cid=186&Itemid=3&limitstart=" +
      String.valueOf((v - 1) * 20), v))
  }
}
