//package it.dtk.db
//
//import akka.actor.Props
//import org.joda.time.DateTime
//import it.dtk.db.MongoDbManager._
//import scala.concurrent.duration._
//import scala.language.postfixOps
//import it.dtk.util.MySpec
//
//object DBManagerSpec {
//
//  val newsTest = News(
//    id = None,
//    urlWebSite = Some("http://baritoday.it"),
//    urlNews = Some("http://baritoday.it/cronaca"),
//    title = Some("title"),
//    summary = Some("summary"),
//    newsDate = Some(DateTime.now()),
//    text = Some("text"),
//    tags = Some(Set("pippo", "pluto"))
//  )
//
//  val controllerTest = WebControllerData(
//    controllerName = Some("BariTodayWebSiteController"),
//    stopUrls = Some(List()),
//    enabled = Some(true)
//  )
//}
//
//class DBManagerSpec extends MySpec("DBManagerSpec") {
//
//  import DBManagerSpec._
//
//  "An DBManager actor (hosted on 10.1.0.62)" must {
//
//    val dbActor = system.actorOf(Props(classOf[MongoDbManager], "10.1.0.62", "testNews"))
//
//    "insert a news" in {
//      dbActor ! InsertNews(newsTest)
//      expectMsg(10 seconds, DoneInsertNews)
//    }
//
//    "list enabled web controllers" in {
//      dbActor ! ListWebControllers
//      val res = expectMsgClass(10 seconds, classOf[WebControllers])
//      assert(res.controllers.nonEmpty)
//    }
//
//    "enable a web controller" in {
//      dbActor ! UpdateWebController(controllerTest)
//      expectMsg(10 seconds, DoneUpdateWebController)
//    }
//  }
//}
