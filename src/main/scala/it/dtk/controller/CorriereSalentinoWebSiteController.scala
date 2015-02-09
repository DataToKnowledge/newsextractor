package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import akka.actor.ActorRef
import it.dtk.extractor.CorriereSalentinoDataRecordExtractor

object CorriereSalentinoWebSiteController {
  def props(name: String) =
    Props(classOf[CorriereSalentinoWebSiteController],name)
}

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class CorriereSalentinoWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 718
  //override val maxIndex: Int = 5

  override val call = 2

  override val baseUrl: String = "http://www.corrieresalentino.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[CorriereSalentinoDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "category/cronaca/page/" + currentIndex
}
