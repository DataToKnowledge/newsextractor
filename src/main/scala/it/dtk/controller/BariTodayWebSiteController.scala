package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.BariTodayDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BariTodayWebSiteController(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef)
  extends WebSiteController(id, dbActor, routerHttpGetter) {

  override val maxIndex: Int = 299
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.baritoday.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[BariTodayDataRecordExtractor],routerHttpGetter)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "cronaca/pag/" + currentIndex

}
