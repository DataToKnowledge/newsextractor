package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.RepubblicaDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class RepubblicaWebSiteController(dbManager: ActorRef) extends WebSiteController {

  override val dbActor = dbManager

  //override val maxIndex: Int = 12
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://bari.repubblica.it/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[RepubblicaDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = {
    baseUrl + "cronaca/" + currentIndex
  }
}
