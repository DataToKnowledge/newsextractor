package it.dtk

import akka.actor.{Props, Actor, ActorLogging}
import it.dtk.controller.{LeccePrimaWebSiteController, GoBariWebSiteController}

/**
 * Author: Michele Damiano Torelli
 * Project: NewsExtractor
 * Date: 05/02/14
 * Time: 18:42
 */
class Main extends Actor with ActorLogging {

  val controller = context.actorOf(Props[LeccePrimaWebSiteController])
  controller ! WebSiteController.Start

  override def receive: Actor.Receive = {
    case _ => print("mah")
  }
}
