package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.GiornaleDiPugliaDataRecordExtractor
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class GiornaleDiPugliaWebSiteController(dbManager: ActorRef) extends WebSiteController {

  override val dbActor = dbManager

  //override val maxIndex: Int = 590
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.giornaledipuglia.com/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[GiornaleDiPugliaDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String= 
    baseUrl + "search/label/CRONACA#pgn=" + currentIndex
  
}
