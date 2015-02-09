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
//object NewsPugliaDataRecordExtractorSpec {
//
//  val url = "http://www.newspuglia.it/"
//
//  val html = Source.fromFile("./src/test/resources/NewsPugliaCronacaList.html", "UTF-8").getLines().mkString
//
//  val date = DateTime.now()
//
//}
//
//class NewsPugliaDataRecordExtractorSpec extends MySpec("NewsPugliaDataRecordExtractorSpec") {
//
//  import NewsPugliaDataRecordExtractorSpec._
//
//  val parent = system.actorOf(Props(new Actor {
//    val child = context.actorOf(Props(classOf[NewsPugliaDataRecordExtractor], ActorRef.noSender), "child")
//
//    def receive = {
//      case x if sender == child => testActor forward x
//      case x => child forward x
//    }
//  }
//  ))
//
//  "the NewsPuglia record extractor" should {
//
//    "extract 11 data records" in {
//
//      parent ! Got(url, html, date)
//      val results = expectMsgClass(15.seconds, classOf[DataRecords])
//
//      assert(results.dataRecords.size == 11)
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
