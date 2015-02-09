package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.BrindisiLiberaDataRecordExtractor
import akka.actor.ActorRef

object BrindisiLiberaWebSiteController {
  def props(name: String) =
    Props(classOf[BrindisiLiberaWebSiteController],name)
}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiLiberaWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 7
  //override val maxIndex: Int = 5

  override val call = 2

  override val baseUrl: String = "http://www.brindisilibera.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[BrindisiLiberaDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "category/cronaca/page/" + currentIndex
}
