package it.dtk.db

import akka.actor.{ Props, ActorSystem }
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }
import org.joda.time.DateTime
import it.dtk.db.DBManager._
import scala.concurrent.duration._
import scala.language.postfixOps

object DBManagerSpec {

  val newsTest = News(
    id = None,
    urlWebSite = Some("http://baritoday.it"),
    urlNews = Some("http://baritoday.it/cronaca"),
    title = Some("title"),
    summary = Some("summary"),
    newsDate = Some(DateTime.now()),
    text = Some("text"),
    tags = Some(Set("pippo", "pluto"))
  )

  val controllerTest = WebControllerData(
    controllerName = Some("BariTodayWebSiteController"),
    stopUrls = Some(List()),
    enabled = Some(true)
  )
}

class DBManagerSpec extends TestKit(ActorSystem("DBManagerSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  import DBManagerSpec._

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "An DBManager actor" must {

    val dbActor = system.actorOf(Props(classOf[DBManager], "10.1.0.62", "testNews"))

    "insert a news" in {
      dbActor ! InsertNews(newsTest)
      expectMsg(10 seconds, DoneInsertNews)
    }

    "list web controllers" in {
      dbActor ! ListWebControllers
      expectMsgClass(10 seconds, classOf[WebControllers])
    }

    "update web controllers" in {
      dbActor ! UpdateWebController(controllerTest)
      expectMsg(10 seconds, DoneUpdateWebController)
    }
  }
}
