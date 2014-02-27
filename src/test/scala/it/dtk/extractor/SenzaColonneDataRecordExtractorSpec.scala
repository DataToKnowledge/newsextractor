package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.util.StepParent
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object SenzaColonneDataRecordExtractorSpec{
  
  val url = "http://www.senzacolonnenews.it/cronaca.html?start=5"

  val date = DateTime.now().toDate
}

class SenzaColonneDataRecordExtractorSpec extends MySpec("SenzaColonneDataRecordExtractorSpec") {

  import BrindisiLiberaDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/SenzaColonne.html", "UTF-8").getLines().mkString
  
  "The Brindisi Libera record extractor" should {
    "extract 9 data records" in {
      val dataRecordProps = Props(classOf[SenzaColonneDataRecordExtractor], url, html, date)
      val dataRecordActor = system.actorOf(Props(classOf[StepParent], dataRecordProps, testActor))
      val results = expectMsgClass(15.seconds,classOf[DataRecords])

      assert(results.dataRecords.size == 5)
      results.dataRecords.foreach(dr => {
      	assert(dr.title.length() > 0)   
        println(dr.title)	
      }
      	)
    }
  }
}