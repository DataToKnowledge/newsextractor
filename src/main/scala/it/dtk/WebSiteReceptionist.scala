package it.dtk

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props, Terminated}
import com.typesafe.config.ConfigFactory
import it.dtk.DataModel._

import scala.collection.mutable.Map
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Author: Michele Damiano Torelli and Fabio Fumarola
 * Project: NewsExtractor
 * Date: 05/02/14
 * Time: 18:42
 */

object WebSiteReceptionist {

  case object Start

  case object Stop

  def props = Props(classOf[WebSiteReceptionist])

}

class WebSiteReceptionist extends Actor with ActorLogging {

  import it.dtk.MongoDbActor._
  import it.dtk.WebSiteReceptionist._

  /**
   * @return supervisor strategy for dbmanager and for http router
   */
  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 minutes) {
    case ex: java.io.IOException =>
      log.error("Supervisor error from {} with {}", sender.path.name, ex.getMessage())
      Restart

    case ex: Exception =>
      log.error("Supervisor error from {} with {}", sender.path.name, ex.getMessage())
      Restart
  }

  val dbActor: ActorRef = context.actorOf(MongoDbActor.props)
  val httpGetterRouter = context.actorOf(HttpGetter.routerProps())
  var controllersMap = Map[String, CrawledWebSites]()

  def receive = {

    case Start =>
      log.info("Let's Start again :)")
      dbActor ! ListWebControllers

    case DBFailure(ListWebControllers, ex) =>
      log.error("cannot connect to mongod when listing for controllers", ex)
      log.error("Stopping the world !!!")
      context.stop(self)

    case WebControllers(controllers) =>
      log.info("Start news extraction for {} controllers", controllers.size)

      controllers.foreach { c =>

        for (id <- c.id; contrName <- c.controllerName) {

          try {
            //FIXME make a method and make readable
            val contrClass = Class.forName(s"it.dtk.controller.$contrName")
            val controllerActor = context.child(contrName).
              getOrElse(context.actorOf(Props(contrClass, id.toString(), dbActor, httpGetterRouter), contrName))

            val stopUrls = c.stopUrls.getOrElse(List()).toVector
            controllerActor ! WebSiteController.Start(stopUrls)

            controllersMap += id.toString -> c
          } catch {
            case ex: Throwable =>
              log.error("Controller {} is not available", contrName, ex)
          }
        }
      }

    case WebSiteController.Done(idController, extractedUrls) =>
      val optController = controllersMap.get(idController)
      log.info("Extracted {} urls from the controller with id {}",
        extractedUrls.size, optController.get.controllerName)

      optController.foreach { c =>
        val nextStopUrls = extractedUrls.take(3)

        if (nextStopUrls.nonEmpty) {
          val toUpdateController = c.copy(stopUrls = Option(nextStopUrls.toList))
          dbActor ! MongoDbActor.UpdateWebController(toUpdateController)

          controllersMap += toUpdateController.id.get.toString() -> toUpdateController

        }

      }

    case WebSiteController.Fail(idController, currentIndex, extractedUrls) =>
      val optController = controllersMap.get(idController)
      log.error("WebSiteController {} fails for index {}", optController.get.controllerName, currentIndex)

    case Terminated(ref) =>
      log.error("terminated actor {}", ref.path)
  }
}