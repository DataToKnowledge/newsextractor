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

  //override val maxIndex: Int = 36
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.brindisilibera.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[BrindisiLiberaDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = 
    baseUrl + "new/index.php?limitstart=" + String.valueOf((currentIndex - 1) * 35)
}
