package it.dtk

import akka.actor.{ Props, Actor, ActorLogging }
import it.dtk.controller.LeccePrimaWebSiteController
import akka.actor.ActorSystem

/**
 * Author: Michele Damiano Torelli
 * Project: NewsExtractor
 * Date: 05/02/14
 * Time: 18:42
 */
class MainActor extends Actor with ActorLogging {

  val controller = context.actorOf(Props[LeccePrimaWebSiteController])
  controller ! WebSiteController.Start

  override def receive: Actor.Receive = {
    case _ => print("mah")
  }
}

object Main {

  def main(args: Array[String]) {
    val system = ActorSystem("Main")
    val ac = system.actorOf(Props[MainActor])
  }
}