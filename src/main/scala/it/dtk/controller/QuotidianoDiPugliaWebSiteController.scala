package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.QuotidianoDiPugliaDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class QuotidianoDiPugliaWebSiteController(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef)
  extends WebSiteController(id, dbActor, routerHttpGetter) {

  override val maxIndex: Int = 1

  override val baseUrl: String = "http://www.quotidianodipuglia.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[QuotidianoDiPugliaDataRecordExtractor],routerHttpGetter)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "leggitutte.php?sez=HOME"

}
