package it.dtk.db

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import it.dtk.db.DBManager.{Killed, Fail, Done}

class DBManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("DBManagerSpec"))

  private val dbActor = system.actorOf(Props(classOf[DBManager], "localhost", 27017, "test"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An DBManager actor" must {

    "persist data successfully" in {
      dbActor ! "hello"
      expectMsgClass(Done.getClass)
    }

    "notify a failure" in {
      dbActor ! "fail!"
      expectMsgClass(Fail.getClass)
    }

    "kill himself if requested" in {
      dbActor ! "kill"
      expectMsgClass(Killed.getClass)
    }
  }
}
