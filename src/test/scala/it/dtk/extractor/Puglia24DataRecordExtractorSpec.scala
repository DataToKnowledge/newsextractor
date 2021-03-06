//package it.dtk.extractor
//
//import it.dtk.util.MySpec
//import org.joda.time.DateTime
//import akka.actor.{Actor, ActorRef, Props}
//import scala.concurrent.duration._
//import scala.io.Source
//import it.dtk.HttpGetter._
//import it.dtk.DataRecordExtractor.DataRecords
//
//object Puglia24DataRecordExtractorSpec {
//
//  val url = "http://www.puglia24news.it/"
//
//  val html = Source.fromFile("./src/test/resources/Puglia24CronacaList.html", "UTF-8").getLines().mkString
//
//  val date = DateTime.now()
//
//}
//
//class Puglia24DataRecordExtractorSpec extends MySpec("Puglia24DataRecordExtractorSpec") {
//
//  import Puglia24DataRecordExtractorSpec._
//
//  val parent = system.actorOf(Props(new Actor {
//    val child = context.actorOf(Props(classOf[Puglia24DataRecordExtractor], ActorRef.noSender), "child")
//
//    def receive = {
//      case x if sender == child => testActor forward x
//      case x => child forward x
//    }
//  }
//  ))
//
//  "the Puglia24 record extractor" should {
//
//    "extract 6 data records" in {
//
//      parent ! Got(url, html, date)
//      val results = expectMsgClass(15.seconds, classOf[DataRecords])
//
//      assert(results.dataRecords.size == 6)
//      results.dataRecords.foreach(dr =>
//        assert(dr.title.nonEmpty)
//      )
//      results.dataRecords.foreach(dr =>
//        assert(dr.summary.nonEmpty)
//      )
//      results.dataRecords.foreach(dr =>
//        assert(dr.newsUrl.nonEmpty)
//      )
//    }
//  }
//}
//
//
//
//
//
//
//
//
//
