package it.dtk

import akka.actor.{ Props, Actor, ActorLogging }
import akka.actor.ActorSystem
import it.dtk.db.DBManager
import akka.actor.ActorRef
import it.dtk.controller.BariTodayWebSiteController
import it.dtk.controller.GoBariWebSiteController


/**
 * Author: Michele Damiano Torelli
 * Project: NewsExtractor
 * Date: 05/02/14
 * Time: 18:42
 */
class WebSiteReceptionist extends Actor with ActorLogging {
  
  def host = "localhost"
  def db = "dbNews"
    
  val dbActor: ActorRef = context.actorOf(Props(classOf[DBManager], host, db))
  
  val controller: ActorRef = context.actorOf(Props(classOf[GoBariWebSiteController], dbActor), "GoBariWebSiteController")
  //val controller = context.actorOf(Props(classOf[LeccePrimaWebSiteController],dbActor))
  
  controller ! WebSiteController.Start(None)

  override def receive: Actor.Receive = {
    case WebSiteController.Done(baseUrl) => print(baseUrl)
  }
}

object Main {

  def main(args: Array[String]) {
    val system = ActorSystem("Main")
    val ac = system.actorOf(Props[WebSiteReceptionist])
  }
}