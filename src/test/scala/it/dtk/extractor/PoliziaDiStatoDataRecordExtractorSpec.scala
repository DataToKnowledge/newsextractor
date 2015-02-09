//package it.dtk.extractor
//
//import scala.concurrent.duration.DurationInt
//import scala.io.Source
//
//import org.joda.time.DateTime
//
//import akka.actor.Actor
//import akka.actor.ActorRef
//import akka.actor.Props
//import akka.actor.actorRef2Scala
//import it.dtk.DataRecordExtractor.DataRecords
//import it.dtk.HttpGetter._
//import it.dtk.util.MySpec
//
//object PoliziaDiStatoDataRecordExtractorSpec {
//
//  val url = "http://www.poliziadistato.it/"
//
//  val html = Source.fromFile("./src/test/resources/PoliziaDiStatoNewsList.html", "UTF-8").getLines().mkString
//
//  val date = DateTime.now()
//
//}
//
//class PoliziaDiStatoDataRecordExtractorSpec extends MySpec("PoliziaDiStatoDataRecordExtractorSpec") {
//
//  import PoliziaDiStatoDataRecordExtractorSpec._
//
//  val parent = system.actorOf(Props(new Actor {
//    val child = context.actorOf(Props(classOf[PoliziaDiStatoDataRecordExtractor], ActorRef.noSender), "child")
//
//    def receive = {
//      case x if sender == child => testActor forward x
//      case x => child forward x
//    }
//  }))
//
//  "The PoliziaDiStato record extractor" should {
//
//    "extract 10 data records" in {
//      parent ! Got(url, html, date)
//
//      val results = expectMsgClass(15.seconds, classOf[DataRecords])
//
//      assert(results.dataRecords.size == 10)
//      results.dataRecords.foreach(dr =>
//        assert(dr.title.nonEmpty))
//      results.dataRecords.foreach(dr =>
//        assert(dr.summary.nonEmpty))
//      results.dataRecords.foreach(dr =>
//        assert(dr.newsUrl.nonEmpty))
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
