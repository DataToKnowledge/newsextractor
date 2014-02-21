package it.dtk

import akka.actor.{Props, Actor, ActorLogging}
import it.dtk.controller.GoBariWebSiteController

/**
 * Author: Michele Damiano Torelli
 * Project: NewsExtractor
 * Date: 05/02/14
 * Time: 18:42
 */
class Main extends Actor with ActorLogging {
  val controller = context.actorOf(Props[GoBariWebSiteController])
  controller ! WebSiteController.Start

  def receive = {
    case x:AnyRef => println(x)
  }
}
