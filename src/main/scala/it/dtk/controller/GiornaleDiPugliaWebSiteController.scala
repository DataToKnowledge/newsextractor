package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.GiornaleDiPugliaDataRecordExtractor
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class GiornaleDiPugliaWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 590
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.giornaledipuglia.com/"

  override def dataRecordExtractorProps(url: String, html: String, date: DateTime): Props = Props(classOf[GiornaleDiPugliaDataRecordExtractor],url,html,date)

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "search/label/CRONACA#pgn=" + v, v))
  }
}
