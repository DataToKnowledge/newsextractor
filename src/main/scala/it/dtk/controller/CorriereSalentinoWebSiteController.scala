package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import akka.actor.ActorRef
import it.dtk.extractor.CorriereSalentinoDataRecordExtractor

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class CorriereSalentinoWebSiteController(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef)
  extends WebSiteController(id, dbActor, routerHttpGetter) {

  override val maxIndex: Int = 40
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.corrieresalentino.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[CorriereSalentinoDataRecordExtractor],routerHttpGetter)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "category/cronaca/page/" + currentIndex
}
