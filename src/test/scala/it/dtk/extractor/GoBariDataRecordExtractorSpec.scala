package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.util.StepParent
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object GoBariDataRecordExtractorSpec {

  val url = "http://go-bari.it/notizie/cronaca/"

  val date = DateTime.now()

}

class GoBariDataRecordExtractorSpec extends MySpec("GoBariDataRecordExtractorSpec") {

  import GoBariDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/GoBariCronacaList.html", "iso-8859-1").getLines().mkString
  
  "The GoBari record extractor" should {
    "extract 20 data records" in {
      val dataRecordProps = Props(classOf[GoBariDataRecordExtractor], url, html, date)
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], dataRecordProps, testActor))
      val results = expectMsgClass(15.seconds,classOf[DataRecords])

      assert(results.dataRecords.size == 20)
      results.dataRecords.foreach(dr =>
      	assert(dr.title.length() > 0)
      )
    }
  }
}









