package it.dtk.extractor

import it.dtk.util.MySpec
import org.joda.time.DateTime
import akka.actor.{Actor, ActorRef, Props}
import scala.concurrent.duration._
import scala.io.Source
import it.dtk.HttpGetter.Result
import it.dtk.DataRecordExtractor.DataRecords

object RepubblicaDataRecordExtractorSpec {

  val url = "http://bari.repubblica.it/"

  val html = Source.fromFile("./src/test/resources/BariRepubblicaCronacaList.html", "UTF-8").getLines().mkString

  val date = DateTime.now()

}

class RepubblicaDataRecordExtractorSpec extends MySpec("RepubblicaDataRecordExtractorSpec") {

  import RepubblicaDataRecordExtractorSpec._

  val parent = system.actorOf(Props(new Actor {
    val child = context.actorOf(Props(classOf[RepubblicaDataRecordExtractor], ActorRef.noSender), "child")

    def receive = {
      case x if sender == child => testActor forward x
      case x => child forward x
    }
  }
  ))

  "The BariRepubblica record extractor" should {

    "extract 10 data records" in {

      parent ! Result(url, html, date)
      val results = expectMsgClass(15.seconds, classOf[DataRecords])

      assert(results.dataRecords.size == 10)
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









