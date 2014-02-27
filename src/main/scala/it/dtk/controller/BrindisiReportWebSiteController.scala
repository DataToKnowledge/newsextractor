package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.BrindisiReportDataRecordExtractor

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiReportWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 717
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.brindisireport.it/cronaca/page/"

  override def dataRecordExtractorProps(url: String, html: String, date: Date): Props = Props(classOf[BrindisiReportDataRecordExtractor],url,html,date)

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + v, v))
  }
}
