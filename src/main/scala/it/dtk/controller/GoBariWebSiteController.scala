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

  //override val maxIncrement: Int = 162
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://go-bari.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: DateTime): Props = Props(classOf[GoBariDataRecordExtractor], url, html, date)

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "index.php?id=0%7C2&idS=19&pageID=" + v, v))
  }
}
