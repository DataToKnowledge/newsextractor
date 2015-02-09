package it.wheretolive.newsextractor

import java.util.Locale

import akka.actor.Actor.Receive
import akka.actor.{ Actor, ActorLogging, Props }
import akka.routing.RoundRobinPool
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import logic._
import MessageProtocol._
import akka.pattern._

object HttpActor {
  def routerProps(nrOfInstances: Int = 2) =
    RoundRobinPool(nrOfInstances).props(props)

  def props = Props(classOf[HttpActor])

  val sdf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z").withLocale(Locale.ENGLISH)
}

/**
 * Created by fabiofumarola on 08/02/15.
 */
class HttpActor extends Actor with ActorLogging with HttpExecutor {
  import HttpActor._

  import context.dispatcher

  override def receive: Receive = {

    case HttpRequest(url) =>
      get(url).map { res =>
        val date = if (res.getHeader("Date") != null) sdf.parseDateTime(res.getHeader("Date")) else DateTime.now()
        HttpResponse(
          url = url,
          html = res.getResponseBody,
          date = date
        )
      } pipeTo sender
  }

  override def postStop(): Unit = {
    shutdown()
  }
}
