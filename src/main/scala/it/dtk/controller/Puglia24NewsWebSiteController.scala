package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.Puglia24DataRecordExtractor
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class Puglia24NewsWebSiteController(dbManager: ActorRef) extends WebSiteController {

  override val dbActor = dbManager

  //override val maxIndex: Int = 133
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.puglia24news.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[Puglia24DataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = 
    baseUrl + "category/cronaca/page/" + currentIndex
}
