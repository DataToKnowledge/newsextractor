package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.QuotidianoDiPugliaDataRecordExtractor
import akka.actor.ActorRef

object QuotidianoDiPugliaWebSiteController{
  def props(name: String) =
    Props(classOf[Puglia24NewsWebSiteController],name)
}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class QuotidianoDiPugliaWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 173
  override val call = 2

  override val baseUrl: String = "http://www.quotidianodipuglia.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[QuotidianoDiPugliaDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "?p=search&tag=&q=&n=" + currentIndex

}
