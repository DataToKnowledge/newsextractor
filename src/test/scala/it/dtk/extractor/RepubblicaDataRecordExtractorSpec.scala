package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.util.StepParent
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source
import it.dtk.extractor._

object RepubblicaDataRecordExtractorSpec {

  val url = "http://bari.repubblica.it/cronaca/"

  val date = DateTime.now()

}

class RepubblicaDataRecordExtractorSpec extends MySpec("RepubblicaDataRecordExtractorSpec") {

  import RepubblicaDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/BariRepubblicaCronacaList.html", "UTF-8").getLines().mkString
  
  "The BariRepubblica record extractor" should {
    "extract 10 data records" in {
      val dataRecordProps = Props(classOf[RepubblicaDataRecordExtractor], url, html, date)
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], dataRecordProps, testActor))
      val results = expectMsgClass(15.seconds,classOf[DataRecords])

      assert(results.dataRecords.size == 10)
      results.dataRecords.foreach(dr =>
      	assert(dr.title.length() > 0)
      )
    }
  }
}









