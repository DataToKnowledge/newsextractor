package it.dtk.extractor

import akka.actor.{Actor, ActorRef, Props}
import it.dtk.DataRecordExtractor.DataRecords
import it.dtk.util.MySpec
import org.joda.time.DateTime
import it.dtk.HttpGetter._

import scala.concurrent.duration._
import scala.io.Source

object BrindisiReportDataRecordExtractorSpec {

  val url = "http://www.brindisireport.it/"

  val html = Source.fromFile("./src/test/resources/BrindisiReportCronacaList.html", "UTF-8").getLines().mkString

  val date = DateTime.now()

}

class BrindisiReportDataRecordExtractorSpec extends MySpec("BrindisiReportDataRecordExtractorSpec") {

  import BrindisiReportDataRecordExtractorSpec._

  val parent = system.actorOf(Props(new Actor {
    val child = context.actorOf(Props(classOf[BrindisiReportDataRecordExtractor], ActorRef.noSender), "child")

    def receive = {
      case x if sender == child => testActor forward x
      case x => child forward x
    }
  }
  ))

  "The BrindisiReport record extractor" should {

    "extract 25 data records" in {

      parent ! Got(url, html, date)
      val results = expectMsgClass(15.seconds, classOf[DataRecords])

      assert(results.dataRecords.size == 25)
      results.dataRecords.foreach(dr =>
        assert(dr.title.nonEmpty)
      )
      /*results.dataRecords.foreach(dr =>
        assert(dr.summary.nonEmpty)
      )*/
      results.dataRecords.foreach(dr =>
        assert(dr.newsUrl.nonEmpty)
      )
    }
  }
}









