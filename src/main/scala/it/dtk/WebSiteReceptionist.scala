package it.dtk

import akka.actor.SupervisorStrategy._
import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props, Terminated }
import com.typesafe.config.ConfigFactory
import it.dtk.DataModel._
import it.dtk.controller.ControllersMapper

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
        val controllerName = c.controllerName.get
        dbActor ! LastUrlNewsRequest(controllerName, c.urlWebSite.get)
      }

    case LastUrlNewsResponse(controllerName, url) =>

      val controllerActor = context.child(controllerName).
        getOrElse(context.actorOf(ControllersMapper.props(controllerName).get, controllerName))
      controllerActor ! WebSiteController.Start(url)

    case LastUrlNewsFailure(controllerName, ex) =>
      log.error("failed extracting last url for {}", controllerName, ex)

    case WebSiteController.Done(name, extractedUrls) =>
      log.info("Extracted {} urls from the controller with id {}", extractedUrls.size, name)

    case WebSiteController.Fail(name, currentIndex, extractedUrls) =>
      log.error("WebSiteController {} fails for index {}", name, currentIndex)

    case Terminated(ref) =>
      log.error("terminated actor {}", ref.path)
  }
}