package it.dtk

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import java.util.Date
import scala.util.{Failure, Success}

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

      system.actorOf(Props(new StepParent(Props(classOf[HttpGetter], "http://www.google.it/"), testActor)))

      val res = expectMsgClass(classOf[Success[Result]])
      res.get.html.getClass should be(classOf[String])
      res.get.headerDate.getClass should be(classOf[Date])
    }

    "return an empty result when it fetches a 404" in {

      system.actorOf(Props(new StepParent(Props(classOf[HttpGetter], "http://www.google.it/asd"), testActor)))

      val res = expectMsgClass(classOf[Failure[Throwable]])
      a [PageNotFoundException] should be thrownBy res.get
    }

    "return an empty result when it goes in timeout" in {

      system.actorOf(Props(new StepParent(Props(classOf[HttpGetter], "http://www.go.it/"), testActor)))

      expectMsgClass(classOf[Failure[Throwable]])
    }

  }

}

/**
 * Fake parent Actor as TestKit does not set itselfs as parent
 * see http://hs.ljungblad.nu/post/69922869833/testing-parent-child-relationships-in-akka
 *
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class StepParent(child: Props, fwd: ActorRef) extends Actor {
  context.actorOf(child, "child")

  def receive = {
    case msg => fwd.tell(msg, sender())
  }
}