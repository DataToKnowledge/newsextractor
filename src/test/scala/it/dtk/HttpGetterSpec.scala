package it.dtk

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import scala.concurrent.duration._
import java.util.Date

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class HttpGetterSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  import HttpGetter._

  def this() = this(ActorSystem("HttpGetterSpec"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "An HttpGetter actor" must {
    "return the body page" in {
      val httpGetter = system.actorOf(Props(classOf[HttpGetter], "http://www.google.it/"))
      httpGetter ! "get"

      val res = expectMsgClass(10500 millis, classOf[Result])

      res.html.getClass should be (classOf[Some[String]])
      res.headerDate.getClass should be (classOf[Some[Date]])
    }
  }

}
