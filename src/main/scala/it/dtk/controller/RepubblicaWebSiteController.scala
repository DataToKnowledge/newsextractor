package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.RepubblicaDataRecordExtractor
import akka.actor.ActorRef

object RepubblicaWebSiteController {
  def props(name: String) =
    Props(classOf[RepubblicaWebSiteController],name)
}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class RepubblicaWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 12
  //override val maxIndex: Int = 5
  override val call = 2

  override val baseUrl: String = "http://bari.repubblica.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[RepubblicaDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String = {
    baseUrl + "cronaca/" + currentIndex
  }
}
