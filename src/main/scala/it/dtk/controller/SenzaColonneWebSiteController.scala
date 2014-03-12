package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import akka.actor.ActorRef
import it.dtk.extractor.SenzaColonneDataRecordExtractor

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class SenzaColonneWebSiteController(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef)
  extends WebSiteController(id, dbActor, routerHttpGetter) {

  override val maxIndex: Int = 428
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.senzacolonnenews.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[SenzaColonneDataRecordExtractor],routerHttpGetter)

  override def composeUrl(currentIndex: Int): String = {
    baseUrl + "cronaca.html?start=" + String.valueOf((currentIndex - 1) * 5)
  }
}
