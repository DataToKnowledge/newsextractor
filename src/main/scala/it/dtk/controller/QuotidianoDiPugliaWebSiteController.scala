package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.QuotidianoDiPugliaDataRecordExtractor
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class QuotidianoDiPugliaWebSiteController(dbManager: ActorRef) extends WebSiteController {

  override val dbActor = dbManager

  override val maxIndex: Int = 1

  override val baseUrl: String = "http://www.quotidianodipuglia.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[QuotidianoDiPugliaDataRecordExtractor])

  override def composeUrl(currentIndex: Int):String = 
    baseUrl + "leggitutte.php?sez=HOME"
  
}
