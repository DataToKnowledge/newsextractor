package it.dtk.util

import akka.actor.{Actor, ActorRef, Props}

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
