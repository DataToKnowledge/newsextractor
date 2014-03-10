package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.BrindisiLiberaDataRecordExtractor
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiLiberaWebSiteController(dbManager: ActorRef) extends WebSiteController {

  override val dbActor = dbManager

  //override val maxIncrement: Int = 36
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://www.brindisilibera.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: DateTime): Props = Props(classOf[BrindisiLiberaDataRecordExtractor], url, html, date)

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "new/index.php?limitstart=" + String.valueOf((v - 1) * 35), v))
  }
}
