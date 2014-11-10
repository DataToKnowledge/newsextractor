package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.BrindisiLiberaDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiLiberaWebSiteController(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef)
  extends WebSiteController(id, dbActor, routerHttpGetter) {

  override val maxIndex: Int = 7
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.brindisilibera.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[BrindisiLiberaDataRecordExtractor],routerHttpGetter)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "category/cronaca/page/" + currentIndex
}
