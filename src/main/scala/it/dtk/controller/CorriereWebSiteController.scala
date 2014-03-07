package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.WebSiteController.Job
import java.util.Date
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class CorriereWebSiteController extends WebSiteController {

  //override val maxIncrement: Int = 40
  override val maxIncrement: Int = 5

  override val baseUrl: String = "http://corrieredelmezzogiorno.corriere.it/"

  override def dataRecordExtractorProps(url: String, html: String, date: DateTime): Props = ???//Props(classOf[],url,html,date)

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq[Job] = {
    start to stop map (v => Job(baseUrl + "bari/notizie/archivio/cronaca/index.shtml?id=" + String.valueOf(v), v))
  }
}
