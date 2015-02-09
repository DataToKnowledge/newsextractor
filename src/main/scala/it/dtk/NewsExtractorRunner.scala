package it.dtk

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created by fabiofumarola on 07/02/15.
 */
object NewsExtractorRunner {

  def main(args: Array[String]) {

    val config = ConfigFactory.load("newsExtractor.conf")
    val system = ActorSystem("NewsExtractor", config)

    implicit val executor = system.dispatcher

    val receptionist = system.actorOf(WebSiteReceptionist.props, "WebSiteReceptionist")
    system.scheduler.schedule(1 second, 30 minutes, receptionist, WebSiteReceptionist.Start)
  }
}
