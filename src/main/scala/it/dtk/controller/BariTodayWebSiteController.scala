package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.BariTodayDataRecordExtractor
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BariTodayWebSiteController(dbManager: ActorRef) extends WebSiteController {
  
  override val dbActor = dbManager

  //override val maxIndex: Int = 294
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.baritoday.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[BariTodayDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = 
    baseUrl + "cronaca/pag/" + currentIndex

}
