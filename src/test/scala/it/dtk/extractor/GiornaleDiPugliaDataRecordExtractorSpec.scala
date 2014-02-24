package it.dtk.extractor

import it.dtk.DataRecordExtractor
import it.dtk.util.MySpec
import akka.actor.Props
import it.dtk.util.StepParent
import org.joda.time.DateTime
import it.dtk.DataRecordExtractor.DataRecords



object GiornaleDiPugliaRecordExtractorSpec {

  val html = ""

  val url = "http://www.giornaledipuglia.com/search/label/CRONACA/1"

  val date = DateTime.now().toDate()

}

class GiornaleDiPugliaDataRecordExtractorSpec extends MySpec("GiornaleDiPugliaRecordExtractor") {

  import GiornaleDiPugliaRecordExtractorSpec._

  "the giornale di puglia record extractor" should {
    "extract records" in {
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], (Props(classOf[GiornaleDiPugliaDataRecordExtractor], url, html, date), testActor)))
      
      val results = expectMsgClass(classOf[DataRecords])
      println(results)
      //expectMsg(DataRecords(url,date,Seq[DataRecord]()))
    }
  }
}

