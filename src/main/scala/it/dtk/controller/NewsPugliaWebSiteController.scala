package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.NewsPugliaDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class NewsPugliaWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 11
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.newspuglia.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[NewsPugliaDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "index.html?start=" + String.valueOf(((currentIndex - 1) * 10) + 1)

  override val call: Int = 2
}
