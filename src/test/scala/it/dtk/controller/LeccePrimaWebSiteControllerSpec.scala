package it.dtk.controller

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import it.dtk.WebSiteController.Start

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class LeccePrimaWebSiteControllerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("LeccePrimaWebSiteControllerSpec"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A LeccePrimaWebSiteController actor" must {

    "return the next links" in {

      val controller = system.actorOf(Props(classOf[LeccePrimaWebSiteController]))

      controller ! Start
    }
  }
}
