package it.dtk.extractor

import it.dtk.util.MySpec
import org.joda.time.DateTime
import akka.actor.Props
import it.dtk.DataRecordExtractor
import scala.concurrent.duration._
import scala.io.Source

object CorriereDataRecordExtractorSpec {

  val url = "http://corrieredelmezzogiorno.corriere.it/bari/notizie/archivio/cronaca/index.shtml"

  val date = DateTime.now()

}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class CorriereDataRecordExtractorSpec extends MySpec("CorriereDataRecordExtractorSpec") {

  import CorriereDataRecordExtractorSpec._
  import DataRecordExtractor._

  val html = Source.fromFile("./src/test/resources/CorriereCronacaList.html", "ISO-8859-1").getLines().mkString

  "The Corriere record extractor" should {

    "extract 20 data records" in {
      val dataRecordProps = Props(classOf[CorriereDataRecordExtractor], url, html, date)
      val results = expectMsgClass(15.seconds, classOf[DataRecords])

      assert(results.dataRecords.size == 20)
      results.dataRecords.foreach(dr =>
        assert(!dr.title.isEmpty)
      )
      results.dataRecords.foreach(dr =>
        assert(!dr.summary.isEmpty)
      )
      results.dataRecords.foreach(dr =>
        assert(!dr.newsUrl.isEmpty)
      )
    }
  }
}










