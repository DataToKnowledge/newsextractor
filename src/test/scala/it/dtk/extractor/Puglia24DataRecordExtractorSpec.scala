package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.util.StepParent
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object Puglia24DataRecordExtractorSpec {

  val url = "http://www.puglia24news.it/category/cronaca/"

  val date = DateTime.now()

}

class Puglia24DataRecordExtractorSpec extends MySpec("Puglia24DataRecordExtractorSpec") {

  import Puglia24DataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/Puglia24CronacaList.html", "UTF-8").getLines().mkString
  
  "the puglia 24 record extractor" should {
    "extract records 25 data records" in {
      val dataRecordProps = Props(classOf[Puglia24DataRecordExtractor], url, html, date)
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], dataRecordProps, testActor))
      val results = expectMsgClass(15.seconds,classOf[DataRecords])
      assert(results.dataRecords.size == 7)
      results.dataRecords.foreach(dr =>
      	assert(dr.title.length() > 0)
      )
    }
  }
}









