package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import org.joda.time.DateTime
import akka.actor.ActorRef
import it.dtk.extractor.CorriereDataRecordExtractor

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class CorriereWebSiteController(dbManager: ActorRef) extends WebSiteController {

  override val dbActor = dbManager

  //override val maxIndex: Int = 40
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://corrieredelmezzogiorno.corriere.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[CorriereDataRecordExtractor])

  override def composeUrl(currentIndex: Int):  String = 
    baseUrl + "bari/notizie/archivio/cronaca/index.shtml?id=" + currentIndex
}
