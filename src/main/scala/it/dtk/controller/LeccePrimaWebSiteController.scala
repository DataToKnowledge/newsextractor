package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.LeccePrimaDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class LeccePrimaWebSiteController(id: String,dbActor: ActorRef) extends WebSiteController(id,dbActor) {

  override val maxIndex: Int = 851
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.lecceprima.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[LeccePrimaDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = 
    baseUrl + "cronaca/pag/" + currentIndex
}
