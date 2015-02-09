package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.GoBariDataRecordExtractor
import akka.actor.ActorRef

object GoBariWebSiteController {
  def props(name: String) =
    Props(classOf[GoBariWebSiteController],name)
}

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it> and Fabio Fumarola
 */

class GoBariWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 216
  //override val maxIndex: Int = 5
  override val call = 2

  override val baseUrl: String = "http://go-bari.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[GoBariDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String = {
    baseUrl + "index.php?id=0%7C2&idS=19&pageID=" + currentIndex
  }
}
