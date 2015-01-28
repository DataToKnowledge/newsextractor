//package it.dtk
//
//import it.dtk.util.MySpec
//import akka.actor.{Actor, Props}
//import scala.concurrent.duration._
//import it.dtk.MainContentExtractor.Result
//import it.dtk.db.DataModel._
//import scala.io.Source
//import org.joda.time.DateTime
//import it.dtk.HttpGetter.Get
//
//object MainContentExtractorSpec {
//
//  val title = "Giovani, anziani, asili nido e soldi per il Sud ecco il progetto del governo per l'equità"
//
//  val urlNews = "http://www.repubblica.it/economia/2012/05/12/news/giovani_anziani_asili_nido_e_soldi_per_il_sud_ecco_il_progetto_del_governo_per_l_equit-34962952/"
//
//  val summary = "summary"
//
//  val date = DateTime.now()
//
//  val news = FetchedNews(urlWebSite = "http://bari.repubblica.it/", urlNews = Some(urlNews), title = Some(title),
//    summary = Some(summary), newsDate = Some(date))
//
//  val html = Source.fromFile("src/test/resources/MainContentExtractor.html", "UTF-8").getLines().mkString
//
//  val htmlNoGoose = Source.fromFile("src/test/resources/MainContentExtractor-noGoose.htm", "UTF-8").getLines().mkString
//
//}
//
//class MainContentExtractorSpec extends MySpec("MainContentExtractorSpec") {
//
//  import MainContentExtractorSpec._
//
//  "A MainContentExtractor actor" should {
//
//    "return the main content using Goose" in {
//
//      val parent = system.actorOf(Props(new Actor {
//        val child = context.actorOf(Props(classOf[MainContentExtractor], news, testActor))
//
//        def receive = {
//          case x if sender == child => testActor forward x
//          case x => child forward x
//        }
//      }))
//
//      // Necessario per non far fallire il test poiche` MainContentExtractor invia questo messaggio appena viene creato
//      expectMsgClass(10.seconds, classOf[Get])
//
//      parent ! HttpGetter.Result(urlNews, html, date)
//
//      val res = expectMsgClass(10.seconds, classOf[Result])
//      assert(res.news.text.get.nonEmpty)
//      //assert(res.news.tags.get.nonEmpty)
//      assert(res.news.metaDescription.get.nonEmpty)
//      assert(res.news.metaKeyword.get.nonEmpty)
//      assert(res.news.canonicalUrl.get.nonEmpty)
//      //assert(res.news.topImage.get.nonEmpty)
//    }
//
//    "return the main content using Boilerpipe when Goose fails" in {
//
//      val parent = system.actorOf(Props(new Actor {
//        val child = context.actorOf(Props(classOf[MainContentExtractor], news, testActor))
//
//        def receive = {
//          case x if sender == child => testActor forward x
//          case x => child forward x
//        }
//      }))
//
//      // Necessario per non far fallire il test poiche` MainContentExtractor invia questo messaggio appena viene creato
//      expectMsgClass(10.seconds, classOf[Get])
//
//      parent ! HttpGetter.Result(urlNews, htmlNoGoose, date)
//
//      val res = expectMsgClass(10.seconds, classOf[Result])
//      assert(res.news.text.get.nonEmpty)
//      //assert(res.news.tags.get.nonEmpty)
//      assert(res.news.metaDescription.get.nonEmpty)
//      assert(res.news.metaKeyword.get.nonEmpty)
//      assert(res.news.canonicalUrl.get.nonEmpty)
//      //assert(res.news.topImage.get.nonEmpty)
//    }
//
//  }
//}