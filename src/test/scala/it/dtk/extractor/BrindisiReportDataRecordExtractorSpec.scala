package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object BrindisiReportDataRecordExtractorSpec {

  val url = "http://www.baritoday.it/cronaca/"

  val date = DateTime.now()

}

class BrindisiReportDataRecordExtractorSpec extends MySpec("BrindisiReportDataRecordExtractorSpec") {

  import BrindisiReportDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/BrindisiReportCronacaList.html", "UTF-8").getLines().mkString
  
  "The Brindisi Report record extractor" should {
    "extract 15 data records" in {
      val dataRecordProps = Props(classOf[BrindisiReportDataRecordExtractor], url, html, date)
      val results = expectMsgClass(15.seconds,classOf[DataRecords])

      assert(results.dataRecords.size == 15)
      results.dataRecords.foreach(dr =>
      	assert(dr.title.length() > 0)
      )
    }
  }
}









