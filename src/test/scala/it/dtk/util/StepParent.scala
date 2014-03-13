package it.dtk.util

import akka.actor.{Actor, ActorRef, Props}

class StepParent(child: Props, fwd: ActorRef) extends Actor {

  context.actorOf(child, "child")

  def receive = {
    case msg => fwd.tell(msg, sender)
  }
}
