package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.LeccePrimaDataRecordExtractor
import akka.actor.ActorRef

object  LeccePrimaWebSiteController {
  def props(name: String) =
    Props(classOf[LeccePrimaWebSiteController],name)
}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class LeccePrimaWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 941
  //override val maxIndex: Int = 5

  override val call = 2

  override val baseUrl: String = "http://www.lecceprima.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[LeccePrimaDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "cronaca/pag/" + currentIndex
}
