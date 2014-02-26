package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.util.StepParent
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object QuotidianoDiPugliaDataRecordExtractorSpec {

  val url = "http://www.quotidianodipuglia.it/leggitutte.php?sez=HOME"

  val date = DateTime.now().toDate

}

class QuotidianoDiPugliaDataRecordExtractorSpec extends MySpec("QuotidianoDiPugliaDataRecordExtractorSpec") {

  import QuotidianoDiPugliaDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/QuotidianoDiPugliaCronacaList.html","latin1").getLines().mkString
  
  "The Quotidiano di Puglia record extractor" should {
    "extract 50 data records" in {
      val dataRecordProps = Props(classOf[ QuotidianoDiPugliaDataRecordExtractor], url, html, date)
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], dataRecordProps, testActor))
      val results = expectMsgClass(15.seconds,classOf[DataRecords])

      assert(results.dataRecords.size == 50)
      results.dataRecords.foreach(dr =>
      	assert(dr.title.length() > 0)
      )
    }
  }
}









