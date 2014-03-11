package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.GiornaleDiPugliaDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class GiornaleDiPugliaWebSiteController(id: String,dbActor: ActorRef) extends WebSiteController(id,dbActor) {

  //override val maxIndex: Int = 590
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.giornaledipuglia.com/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[GiornaleDiPugliaDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String= 
    baseUrl + "search/label/CRONACA#pgn=" + currentIndex
  
}
