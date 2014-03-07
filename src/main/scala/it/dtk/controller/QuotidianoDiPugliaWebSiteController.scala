package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.QuotidianoDiPugliaDataRecordExtractor
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class QuotidianoDiPugliaWebSiteController extends WebSiteController {

  override val maxIncrement: Int = 1

  override val baseUrl: String = "http://www.quotidianodipuglia.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: DateTime): Props = Props(classOf[QuotidianoDiPugliaDataRecordExtractor],url,html,date)

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "leggitutte.php?sez=HOME", v))
  }
}
