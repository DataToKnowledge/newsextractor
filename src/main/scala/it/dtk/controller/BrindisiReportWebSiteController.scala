package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.BrindisiReportDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiReportWebSiteController(id: String,dbActor: ActorRef) extends WebSiteController(id,dbActor) {

  //override val maxIndex: Int = 717
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.brindisireport.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[BrindisiReportDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = 
    baseUrl + "cronaca/page/" + currentIndex
  
}
