package it.dtk

import akka.actor.{ Props, Actor, ActorLogging }
import akka.actor.ActorSystem
import akka.actor.ActorRef
import it.dtk.db.DBManager
import it.dtk.controller._
import it.dtk.db.WebControllerData
import scala.collection.mutable.Map
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Author: Michele Damiano Torelli
 * Project: NewsExtractor
 * Date: 05/02/14
 * Time: 18:42
 */

object WebSiteReceptionist {
  case object Start
  case object Stop
}

class WebSiteReceptionist extends Actor with ActorLogging {

  import WebSiteReceptionist._

  def host = "192.168.0.62"
  def db = "dbNews"

  val dbActor: ActorRef = context.actorOf(Props(classOf[DBManager], host, db))

  val controllersMap: Map[String, WebControllerData] = Map()

  def receive = {

    case Start =>
      dbActor ! DBManager.ListWebControllers

    case DBManager.WebControllers(controllers) =>
      log.info("start doing news extraction for {} controllers", controllers.size)

      controllers.filter(_.enabled.getOrElse(false)).foreach { c =>

        for (id <- c.id; contrName <- c.controllerName) {
          val contrClass = Class.forName(s"it.dtk.controller.$contrName")
          val controllerActor = context.child(contrName).getOrElse(context.actorOf(Props(contrClass, id.toString, dbActor), contrName))

          val stopUrls = c.stopUrls.getOrElse(List()).toVector
          controllerActor ! WebSiteController.Start(stopUrls)
        }

        val controllersList = controllers.map(c => c.id.get.toString -> c)

        //add elements to the map
        controllersMap ++= controllersList.toMap
      }

    case WebSiteController.Done(idController, extractedUrls) =>
      log.info("extracted {} urls from the controller with id {}", extractedUrls.size, idController)
      val optController = controllersMap.get(idController)

      optController.map { c =>
        val nextStopUrls = extractedUrls.take(3)
        if (nextStopUrls.size > 0) {
          val toUpdateController = c.copy(stopUrls = Some(nextStopUrls.toList))
          dbActor ! DBManager.UpdateWebController(toUpdateController)

          //map the toUpdateController in a pair of id -> ControllerData
          val res = toUpdateController.id.map(id => id.toString -> toUpdateController)
          //update the pair in the controllersMap
          res.map(controllersMap += _) //vai mo vai!!!
        }
        
      }

    case DBManager.FailQueryWebControllers(ex) =>
      println(ex)
  }
}

object Main {

  def main(args: Array[String]) {

    //Use the system's dispatcher as ExecutionContext
    import system.dispatcher

    val system = ActorSystem("Main")
    val receptionist = system.actorOf(Props[WebSiteReceptionist])
    system.scheduler.schedule(5 seconds, 5 minutes, receptionist, WebSiteReceptionist.Start)
    //receptionist ! WebSiteReceptionist.Start
  }
}