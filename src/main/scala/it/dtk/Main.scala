package it.dtk

import akka.actor.{ Props, Actor, ActorLogging }
import akka.actor.ActorSystem
import it.dtk.db.DBManager._
import akka.actor.ActorRef
import it.dtk.db.DBManager

/**
 * Author: Michele Damiano Torelli
 * Project: NewsExtractor
 * Date: 05/02/14
 * Time: 18:42
 */
class WebSiteReceptionist extends Actor with ActorLogging {
  
  def host = "192.168.0.62"
  def db = "dbNews"
    
  val dbActor: ActorRef = context.actorOf(Props(classOf[DBManager], host, db))

  dbActor ! Load
  
  //val controller = context.actorOf(Props(classOf[LeccePrimaWebSiteController],dbActor))
  
  //controller ! WebSiteController.Start()

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