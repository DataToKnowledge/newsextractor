package it.dtk.controller

import akka.actor.ActorRef
import akka.actor.Props
import it.dtk.WebSiteController
import it.dtk.extractor.PoliziaDiStatoDataRecordExtractor

object Pages {
    
  val pagesPairs: List[Pair[Int, List[Pair[Int, Int]]]] = List(
      (2002, List((11, 1))),
      (2003, List((6, 1), (10, 1), (12, 1))),
      (2004, List((2, 1), (3, 1), (4, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1), (11, 1))),
      (2005, List((1, 1), (2, 1), (3, 1), (4, 1), (9, 1), (11, 1), (12, 1))),
      (2006, List((1, 1), (2, 1), (3, 2), (4, 2), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1), (11, 1), (12, 2))),
      (2007, List((1, 1), (2, 2), (3, 2), (4, 2), (5, 2), (6, 1), (7, 2), (8, 2), (9, 2), (10, 2), (11, 6), (12, 2))),
      (2008, List((1, 2), (2, 4), (3, 4), (4, 5), (5, 6), (6, 4), (7, 5), (8, 6), (9, 4), (10, 8), (11, 6), (12, 6))),
      (2009, List((1, 5), (2, 4), (3, 5), (4, 5), (5, 7), (6, 4), (7, 6), (8, 4), (9, 7), (10, 8), (11, 8), (12, 7))),
      (2010, List((1, 6), (2, 6), (3, 8), (4, 6), (5, 8), (6, 6), (7, 5), (8, 5), (9, 5), (10, 5), (11, 5), (12, 4))),
      (2011, List((1, 4), (2, 11), (3, 13), (4, 10), (5, 11), (6, 8), (7, 8), (8, 7), (9, 7), (10, 8), (11, 9), (12, 7))),
      (2012, List((1, 9), (2, 9), (3, 10), (4, 8), (5, 9), (6, 7), (7, 9), (8, 9), (9, 7), (10, 9), (11, 8), (12, 7))),
      (2013, List((1, 8), (2, 9), (3, 9), (4, 8), (5, 8), (6, 9), (7, 8), (8, 6), (9, 11), (10, 13), (11, 11), (12, 9))),
      (2014, List((1, 10), (2, 10), (3, 8), (4, 8), (5, 8), (6, 10), (7, 9), (8, 8), (9, 9), (10, 12), (11, 3)))
  )
  
  def extend(pair: Pair[Int, List[Pair[Int, Int]]]): List[String] = pair._2.map(x => pair._1 + "/" + x._1 + "/" + x._2)
  
  val pages: List[String] = pagesPairs.map(p => extend(p)).flatten
  
}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class PoliziaDiStatoWebSiteController(id: String, dbActor: ActorRef, routerHttpGetter: ActorRef)
  extends WebSiteController(id, dbActor, routerHttpGetter) {

  override val maxIndex: Int = 127
  //override val maxIndex: Int = 5

  override val baseUrl: String = "http://www.poliziadistato.it/archivio/category/178/"

  override def dataRecordExtractorProps(): Props =
    Props(classOf[PoliziaDiStatoDataRecordExtractor],routerHttpGetter)

  override def composeUrl(currentIndex: Int): String =
    baseUrl + Pages.pages(currentIndex)

}
