package it.dtk.extractor

import it.dtk.util.MySpec
import java.util.Date
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object NewsPugliaDataRecordExtractorSpec {

  val url = "http://www.newspuglia.it/index.php?option=com_flexicontent&view=category&cid=186&Itemid=3"

  val date = DateTime.now()

}

class NewsPugliaDataRecordExtractorSpec extends MySpec("NewsPugliaDataRecordExtractorSpec") {

  import Puglia24DataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/NewsPugliaCronacaList.html", "UTF-8").getLines.mkString
  
  "the puglia 24 record extractor" should {
    "extract records 25 data records" in {
      val dataRecordProps = Props(classOf[NewsPugliaDataRecordExtractor], url, html, date)
      val results = expectMsgClass(15.seconds,classOf[DataRecords])
      assert(results.dataRecords.size == 20)
      results.dataRecords.foreach(dr =>
      	assert(dr.title.length() > 0)
      )
    }
  }
}









