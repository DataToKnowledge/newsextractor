package it.wheretolive.newsextractor.logic

import akka.actor.{Props, ActorRef}
import it.dtk.extractor.BariTodayDataRecordExtractor

/**
 * Created by fabiofumarola on 08/02/15.
 */
trait BariToday {

  def newsPaperName: String = "BariToday"

  def maxIndex: Int = 359
  //override val maxIndex: Int = 5

  def baseUrl: String = "http://www.baritoday.it/"

  def generateUrl(currentIndex: Int): String =
    baseUrl + "cronaca/pag/" + currentIndex

  def call: Int = 2

  def dataRecordProps: Props = Props()

}
