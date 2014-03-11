package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.Puglia24DataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class Puglia24NewsWebSiteController(id: String,dbActor: ActorRef) extends WebSiteController(id,dbActor) {

  //override val maxIndex: Int = 133
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.puglia24news.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[Puglia24DataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = 
    baseUrl + "category/cronaca/page/" + currentIndex
}
