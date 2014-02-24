package it.dtk.util

import akka.actor.{Actor, ActorRef, Props}
import akka.testkit.TestKit
import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import org.scalatest.WordSpecLike
import org.scalatest.Matchers

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

class MySpec(actorSystemName: String) extends TestKit(ActorSystem(actorSystemName)) with ImplicitSender
  with WordSpecLike with Matchers
