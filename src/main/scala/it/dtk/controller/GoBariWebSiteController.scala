package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.GoBariDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */

class GoBariWebSiteController(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef)
  extends WebSiteController(id, dbActor, routerHttpGetter) {

  override val maxIndex: Int = 168
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://go-bari.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[GoBariDataRecordExtractor],routerHttpGetter)

  override def composeUrl(currentIndex: Int): String = {
    baseUrl + "index.php?id=0%7C2&idS=19&pageID=" + currentIndex
  }
}
