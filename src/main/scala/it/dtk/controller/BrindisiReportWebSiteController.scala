package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.BrindisiReportDataRecordExtractor
import akka.actor.ActorRef
object BrindisiReportWebSiteController {

  def props(name: String) =
    Props(classOf[BrindisiReportWebSiteController],name)
}
/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BrindisiReportWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 522
  //override val maxIndex: Int = 5
  override val call = 2

  override val baseUrl: String = "http://www.brindisireport.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[BrindisiReportDataRecordExtractor], http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "cronaca/pag/" + currentIndex

}
