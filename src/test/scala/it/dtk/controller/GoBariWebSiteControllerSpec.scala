package it.dtk.controller

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import it.dtk.WebSiteController.Start

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class GoBariWebSiteControllerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("GoBariWebSiteControllerSpec"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A GoBariWebSiteController actor" must {

    "return the next links" in {

      val controller = system.actorOf(Props(classOf[GoBariWebSiteController]))

      controller ! Start
    }
  }
}
