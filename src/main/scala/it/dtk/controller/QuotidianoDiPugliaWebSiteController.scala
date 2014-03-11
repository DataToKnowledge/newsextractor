package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.QuotidianoDiPugliaDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class QuotidianoDiPugliaWebSiteController(id: String,dbActor: ActorRef) extends WebSiteController(id,dbActor) {

  override val maxIndex: Int = 1

  override val baseUrl: String = "http://www.quotidianodipuglia.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[QuotidianoDiPugliaDataRecordExtractor])

  override def composeUrl(currentIndex: Int):String = 
    baseUrl + "leggitutte.php?sez=HOME"
  
}
