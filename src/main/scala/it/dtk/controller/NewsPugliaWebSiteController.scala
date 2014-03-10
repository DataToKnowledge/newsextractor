package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props
import it.dtk.extractor.NewsPugliaDataRecordExtractor
import akka.actor.ActorRef

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class NewsPugliaWebSiteController(dbManager: ActorRef) extends WebSiteController {

  override val dbActor = dbManager

  //override val maxIndex: Int = 285
  override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.newspuglia.it/"

  override def dataRecordExtractorProps(): Props = 
    Props(classOf[NewsPugliaDataRecordExtractor])

  override def composeUrl(currentIndex: Int): String = 
    baseUrl + "index.php?option=com_flexicontent&view=category&cid=186&Itemid=3&limitstart=" +
      String.valueOf((currentIndex - 1) * 20)
}
