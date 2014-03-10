package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.util.StepParent
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object BariTodayDataRecordExtractorSpec {

  val url = "http://www.baritoday.it/cronaca/"

  val date = DateTime.now()

}

class BariTodayDataRecordExtractorSpec extends MySpec("BariTodayDataRecordExtractorSpec") {

  import BariTodayDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/BariTodayCronacaList.html", "UTF-8").getLines().mkString
  
  "The BariToday record extractor" should {
    "extract 25 data records" in {
      val dataRecordProps = Props(classOf[BariTodayDataRecordExtractor], url, html, date)
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], dataRecordProps, testActor))
      val results = expectMsgClass(15.seconds,classOf[DataRecords])

      assert(results.dataRecords.size == 25)
      results.dataRecords.foreach(dr =>
      	assert(dr.title.length() > 0)
      )
    }
  }
}









