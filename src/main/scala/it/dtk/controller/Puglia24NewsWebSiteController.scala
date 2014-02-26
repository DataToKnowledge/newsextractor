package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.Puglia24DataRecordExtractor

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class Puglia24NewsWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 133
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.puglia24news.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: Date): Props = Props(classOf[Puglia24DataRecordExtractor],url,html,date)

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "category/cronaca/page/" + v, v))
  }
}
