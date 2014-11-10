package it.dtk.extractor

import it.dtk.util.MySpec
import org.joda.time.DateTime
import akka.actor.{Actor, ActorRef, Props}
import scala.concurrent.duration._
import scala.io.Source
import it.dtk.HttpGetter.Result
import it.dtk.DataRecordExtractor.DataRecords

object CorriereSalentinoDataRecordExtractorSpec {

  val url = "http://www.corrieresalentino.it/"

  val html = Source.fromFile("./src/test/resources/CorriereSalentinoCronacaList.html", "ISO-8859-1").getLines().mkString

  val date = DateTime.now()

}

class CorriereSalentinoRecordExtractorSpec extends MySpec("CorriereSalentinoRecordExtractorSpec") {

  import CorriereSalentinoDataRecordExtractorSpec._

  val parent = system.actorOf(Props(new Actor {
    val child = context.actorOf(Props(classOf[CorriereSalentinoDataRecordExtractor], ActorRef.noSender), "child")

    def receive = {
      case x if sender == child => testActor forward x
      case x => child forward x
    }
  }
  ))

  "The CorriereSalentino record extractor" should {

    "extract 18 data records" in {
      parent ! Result(url, html, date)
      val results = expectMsgClass(15.seconds,classOf[DataRecords])

      assert(results.dataRecords.size == 18)
      results.dataRecords.foreach(dr =>
        assert(dr.title.nonEmpty)
      )
      results.dataRecords.foreach(dr =>
        assert(dr.summary.nonEmpty)
      )
      results.dataRecords.foreach(dr =>
        assert(dr.newsUrl.nonEmpty)
      )
    }
  }
}
