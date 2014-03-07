package it.dtk.db

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.joda.time.DateTime
import it.dtk.db.DBManager._
import scala.concurrent.duration._
import scala.language.postfixOps


object DBManagerSpec {
  
  
  //id: Option[BSONObjectID] = None, urlWebSite: Option[String], urlNews: Option[String], title: Option[String], summary: Option[String],
  //newsDate: Option[DateTime], text: Option[String] = None, tags: Option[Set[String]] = None, metaDescription: Option[String] = None,
  //metaKeyword: Option[String] = None, canonicalUrl: Option[String] = None, topImage: Option[String] = None
  
  
  val newsTest = News(None,Some("http://baritoday.it"),Some("http://baritoday.it/cronaca"),Some("title"),Some("summary"),
      Some(DateTime.now()),Some("text"), Some(Set("pippo","pluto")))
}

class DBManagerSpec extends TestKit(ActorSystem("DBManagerSpec")) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  import DBManagerSpec._

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An DBManager actor" must {
    
    val dbActor = system.actorOf(Props(classOf[DBManager], "localhost", "testNews"))

    "persist data successfully" in {
      dbActor ! Insert(newsTest)
      expectMsg(10 minutes, Done)
    }

//    "notify a failure" in {
//      dbActor ! Insert(newsTest)
//      expectMsg(10 minutes, Fail(newsTest))
//    }
  }
}
