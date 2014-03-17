package it.dtk.extractor

import it.dtk.util.MySpec
import org.joda.time.DateTime
import akka.actor.{Actor, ActorRef, Props}
import scala.concurrent.duration._
import scala.io.Source
import it.dtk.HttpGetter.Result
import it.dtk.DataRecordExtractor.DataRecords

object QuotidianoDiPugliaDataRecordExtractorSpec {

  val url = "http://www.quotidianodipuglia.it/"

  val html = Source.fromFile("./src/test/resources/QuotidianoDiPugliaCronacaList.html","latin1").getLines().mkString

  val date = DateTime.now()

}

class QuotidianoDiPugliaDataRecordExtractorSpec extends MySpec("QuotidianoDiPugliaDataRecordExtractorSpec") {

  import QuotidianoDiPugliaDataRecordExtractorSpec._

  val parent = system.actorOf(Props(new Actor {
    val child = context.actorOf(Props(classOf[QuotidianoDiPugliaDataRecordExtractor], ActorRef.noSender), "child")

    def receive = {
      case x if sender == child => testActor forward x
      case x => child forward x
    }
  }
  ))

  "The Quotidiano di Puglia record extractor" should {

    "extract 50 data records" in {

      parent ! Result(url, html, date)
      val results = expectMsgClass(15.seconds, classOf[DataRecords])

      assert(results.dataRecords.size == 50)
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
