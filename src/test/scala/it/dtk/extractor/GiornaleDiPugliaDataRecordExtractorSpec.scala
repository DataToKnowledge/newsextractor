package it.dtk.extractor

import it.dtk.util.MySpec
import akka.actor.Props
import it.dtk.util.StepParent
import org.joda.time.DateTime
import it.dtk.DataRecordExtractor.DataRecords
import scala.concurrent.duration._


object GiornaleDiPugliaRecordExtractorSpec {

  val url = "http://www.giornaledipuglia.com/search/label/CRONACA/1"

  val date = DateTime.now()

}

class GiornaleDiPugliaDataRecordExtractorSpec extends MySpec("GiornaleDiPugliaRecordExtractorSpec") {

  import GiornaleDiPugliaRecordExtractorSpec._

  val html = scala.io.Source.fromFile("src/test/resources/GiornaleDiPugliaCronacaList.html", "UTF-8").getLines().mkString

  "The GiornaleDiPuglia record extractor" should {
    "extract 20 data records" in {
      val dataRecordProps = Props(classOf[GiornaleDiPugliaDataRecordExtractor], url, html, date)
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], dataRecordProps, testActor))
      val results = expectMsgClass(15.seconds, classOf[DataRecords])

      assert(results.dataRecords.size == 20)
      results.dataRecords.foreach(dr =>
        assert(dr.title.length() > 0)
      )
    }
  }
}

