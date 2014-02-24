package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.util.StepParent
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._

object LeccePrimaDataRecordExtractorSpec {

  val html = ""

  val url = "http://www.lecceprima.it/cronaca/"

  val date = DateTime.now().toDate()

}

class LeccePrimaDataRecordExtractorSpec extends MySpec("LeccePrimaRecordExtactor") {

  import LeccePrimaDataRecordExtractorSpec._
  import DataRecordExtractor._

  "the lecce prima record extractor" should {
    "extract records" in {
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], (Props(classOf[LeccePrimaDataRecordExtractor], url, html, date), testActor)))
      val results = expectMsgClass(classOf[DataRecords])
      println(results)
      //expectMsg(DataRecords(url,date,Seq[DataRecord]()))
    }
  }
}









