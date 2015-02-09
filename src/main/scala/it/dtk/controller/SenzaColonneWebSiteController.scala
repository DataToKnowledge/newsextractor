package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import akka.actor.ActorRef
import it.dtk.extractor.SenzaColonneDataRecordExtractor

object SenzaColonneWebSiteController {
  def props(name: String) =
    Props(classOf[SenzaColonneWebSiteController],name)
}
/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class SenzaColonneWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 616
  //override val maxIndex: Int = 5
  override val call = 2

  override val baseUrl: String = "http://www.senzacolonnenews.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[SenzaColonneDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String = {
    baseUrl + "cronaca.html?start=" + String.valueOf((currentIndex - 1) * 5)
  }
}
