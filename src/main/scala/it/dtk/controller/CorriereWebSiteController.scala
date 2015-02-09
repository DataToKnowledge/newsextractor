package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import akka.actor.ActorRef
import it.dtk.extractor.CorriereDataRecordExtractor

object CorriereWebSiteController {
  def props(name: String) =
    Props(classOf[CorriereWebSiteController],name)
}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class CorriereWebSiteController(name: String) extends WebSiteController(name) {

  override val maxIndex: Int = 40
  //override val maxIndex: Int = 5

  override val call = 2

  override val baseUrl: String = "http://corrieredelmezzogiorno.corriere.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[CorriereDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "bari/notizie/archivio/cronaca/index.shtml?id=" + currentIndex
}

object CorriereArchivioWebSiteController {
  def props(name: String) =
    Props(classOf[CorriereArchivioWebSiteController],name)
}

class CorriereArchivioWebSiteController(name: String) extends WebSiteController(name) {
  
    override val maxIndex: Int = 1
  //override val maxIndex: Int = 5

  override val call = 2

  override val baseUrl: String = "http://corrieredelmezzogiorno.corriere.it/"

  override def dataRecordExtractorProps(http: ActorRef): Props =
    Props(classOf[CorriereDataRecordExtractor],http)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + "bari/notizie/cronaca/"
}