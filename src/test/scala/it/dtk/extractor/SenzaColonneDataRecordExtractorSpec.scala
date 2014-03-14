package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object SenzaColonneDataRecordExtractorSpec{
  
  val url = "http://www.senzacolonnenews.it/cronaca.html?start=5"

  val date = DateTime.now()
}

class SenzaColonneDataRecordExtractorSpec extends MySpec("SenzaColonneDataRecordExtractorSpec") {

  import SenzaColonneDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/SenzaColonne.html", "UTF-8").getLines().mkString
  
  "Senza Colonne record extractor" should {
    "extract 5 data records" in {
      val dataRecordProps = Props(classOf[SenzaColonneDataRecordExtractor], url, html, date)
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