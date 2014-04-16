package it.dtk

import akka.actor.{ Props, Actor, ActorLogging }
import akka.actor.ActorSystem
import akka.actor.ActorRef
import it.dtk.db.DBManager
import it.dtk.db.WebControllerData
import scala.collection.mutable.Map
import scala.concurrent.duration._
import scala.language.postfixOps
import akka.routing.RoundRobinRouter
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import akka.actor.Terminated

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

class WebSiteReceptionist(host: String) extends Actor with ActorLogging {

  import WebSiteReceptionist._

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

  def db = "dbNews"

  val dbActor: ActorRef = context.actorOf(DBManager.props(host, db))

  val httpGetterRouter = context.actorOf(Props[HttpGetter].withRouter(RoundRobinRouter(nrOfInstances = 5)))

  val controllersMap: Map[String, WebControllerData] = Map()

  def receive = {

    case Start =>
      log.info("Using MongoDB instance on {}", host)
      dbActor ! DBManager.ListWebControllers

    case DBManager.WebControllers(controllers) =>
      log.info("Start news extraction for {} controllers", controllers.size)

      controllers.filter(_.enabled.getOrElse(false)).foreach { c =>

        for (id <- c.id; contrName <- c.controllerName) {
          val contrClass = Class.forName(s"it.dtk.controller.$contrName")
          val controllerActor = context.child(contrName).
            getOrElse(context.actorOf(Props(contrClass, id.toString(), dbActor, httpGetterRouter), contrName))

          val stopUrls = c.stopUrls.getOrElse(List()).toVector
          controllerActor ! WebSiteController.Start(stopUrls)
        }

        val controllersList = controllers.map(c => c.id.get.toString -> c)

        //add elements to the map
        controllersMap ++= controllersList.toMap
      }

    case WebSiteController.Done(idController, extractedUrls) =>
      val optController = controllersMap.get(idController)
      log.info("Extracted {} urls from the controller with id {}",
        extractedUrls.size, optController.get.controllerName)

      optController.map { c =>
        val nextStopUrls = extractedUrls.take(3)
        if (nextStopUrls.nonEmpty) {
          val toUpdateController = c.copy(stopUrls = Option(nextStopUrls.toList))
          dbActor ! DBManager.UpdateWebController(toUpdateController)

          //map the toUpdateController in a pair of id -> ControllerData
          val res = toUpdateController.id.map(id => id.toString -> toUpdateController)
          //update the pair in the controllersMap
          res.map(controllersMap += _) //vai mo vai!!!
        }

      }

    case WebSiteController.Fail(idController, currentIndex, extractedUrls) =>
      val optController = controllersMap.get(idController)
      log.error("WebSiteController {} fails for index {}", optController.get.controllerName, currentIndex)

    case DBManager.FailQueryWebControllers(ex) =>
      log.error("DBManager FailQueryWebControllers {}", ex.getMessage)
      throw ex

    case WebSiteController.JobUpdate(idController, dataRecordUrl, mainContentUrls) =>
      val optController = controllersMap.get(idController)
      log.info("From the controller {} and the data record URL {} are extract {} urls",
        optController.get.controllerName, dataRecordUrl, mainContentUrls.length)

      if (mainContentUrls.isEmpty)
        log.error("{} failed extracting data records from the URL {}", optController.get.controllerName, dataRecordUrl)

    case Terminated(ref) =>
      log.error("terminated actor {}", ref.path)
  }

}

object Main {

  def main(args: Array[String]) {

    //Use the system's dispatcher as ExecutionContext
    import system.dispatcher

    val system = ActorSystem("NewsExtractor")

    val host = if (args.size > 0) args(0) else "127.0.0.1"

    val receptionist = system.actorOf(Props(classOf[WebSiteReceptionist], host), "WebSiteReceptionist")
    system.scheduler.schedule(1 second, 120 minutes, receptionist, WebSiteReceptionist.Start)
    //receptionist ! WebSiteReceptionist.Start
  }
}