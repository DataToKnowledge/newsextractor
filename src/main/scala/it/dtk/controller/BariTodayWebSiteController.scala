package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.BariTodayDataRecordExtractor
import akka.actor.ActorRef


object BariTodayWebSiteController {
  def props(name: String) = Props(classOf[BariTodayWebSiteController],name)
}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BariTodayWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 359

  override val call = 2

  override val baseUrl: String = "http://www.baritoday.it/"

  override def dataRecordExtractorProps(http: ActorRef) =
   BariTodayDataRecordExtractor.props(http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "cronaca/pag/" + currentIndex

}
