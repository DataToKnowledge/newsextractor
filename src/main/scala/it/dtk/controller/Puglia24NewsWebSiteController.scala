package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.Puglia24DataRecordExtractor
import akka.actor.ActorRef

object Puglia24NewsWebSiteController {
  def props(name: String) =
    Props(classOf[Puglia24NewsWebSiteController],name)
}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class Puglia24NewsWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 129
  //override val maxIndex: Int = 5

  override val call = 2

  override val baseUrl: String = "http://www.puglia24news.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[Puglia24DataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "category/cronaca/page/" + currentIndex
}
