package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import it.dtk.extractor.GoBariDataRecordExtractor
import org.joda.time.DateTime
import akka.actor.ActorRef

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */

class GoBariWebSiteController(dbManager: ActorRef) extends WebSiteController {
  override val dbActor = dbManager

  //override val maxIndex: Int = 162
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://go-bari.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[GoBariDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = {
    baseUrl + "index.php?id=0%7C2&idS=19&pageID=" + currentIndex
  }
}
