package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import akka.actor.ActorRef
import it.dtk.extractor.SenzaColonneDataRecordExtractor

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class SenzaColonneWebSiteController(id: String,dbActor: ActorRef) extends WebSiteController(id,dbActor) {

  //override val maxIndex: Int = 400
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.senzacolonnenews.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[SenzaColonneDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = {
    baseUrl + "cronaca.html?start=" + String.valueOf((currentIndex - 1) * 5)
  }
}
