package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import akka.actor.ActorRef
import it.dtk.extractor.CorriereDataRecordExtractor

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class CorriereWebSiteController(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef)
  extends WebSiteController(id, dbActor, routerHttpGetter) {

  override val maxIndex: Int = 40
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://corrieredelmezzogiorno.corriere.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[CorriereDataRecordExtractor],routerHttpGetter)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "bari/notizie/archivio/cronaca/index.shtml?id=" + currentIndex
}
