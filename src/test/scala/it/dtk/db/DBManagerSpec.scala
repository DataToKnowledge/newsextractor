package it.dtk.db

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import it.dtk.db.DBManager.{Save, Killed, Fail, Done}
import org.joda.time.DateTime

class DBManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("DBManagerSpec"))

  private val dbActor = system.actorOf(Props(classOf[DBManager], "localhost", 27017, "test", "news"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An DBManager actor" must {

    "persist data successfully" in {
      val newsObject = new News(
        id = 1,
        urlWebSite = "http://www.lecceprima.it/cronaca/",
        urlNews = "http://www.lecceprima.it/cronaca/incendio-centro-estetico-merine-27-febbraio-2014.html",
        title = "Scoppia l\'incendio nella cabina sauna. Le fiamme devastano centro estetico.",
        summary = "BLABLABLA",
        newsDate = (new DateTime).toDate
      )

      dbActor ! Save(newsObject)
      expectMsg(Done(newsObject))
    }

 /*   "notify a failure" in {
      dbActor ! "fail!"
      expectMsg(Fail)
    }*/

    "kill himself if requested" in {
      dbActor ! "kill"
      expectMsg(Killed)
    }

  }
}
