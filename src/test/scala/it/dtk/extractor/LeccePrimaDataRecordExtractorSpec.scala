package it.dtk.extractor

import it.dtk.util.MySpec
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.DataRecordExtractor
import scala.io.Source
import scala.concurrent.duration._

object LeccePrimaDataRecordExtractorSpec {

  val url = "http://www.lecceprima.it/cronaca/"

  val date = DateTime.now()
}

class LeccePrimaDataRecordExtractorSpec extends MySpec("LeccePrimaRecordExtactorSpec") {

  import LeccePrimaDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/LeccePrimaCronocaList.html", "UTF-8").getLines().mkString
  
  "The LeccePrima record extractor" should {
    "extract 25 data records" in {
      val dataRecordProps = Props(classOf[LeccePrimaDataRecordExtractor], url, html, date)
      val results = expectMsgClass(15.seconds,classOf[DataRecords])

      assert(results.dataRecords.size == 25)
      results.dataRecords.foreach(dr =>
      	assert(dr.title.length() > 0)
      )
    }
  }
}









