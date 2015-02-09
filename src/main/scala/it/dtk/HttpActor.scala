package it.dtk

import java.util.Locale

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern._
import akka.routing.RoundRobinPool
import it.dtk.logic._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object HttpActor {
  def routerProps(nrOfInstances: Int = 2) =
    RoundRobinPool(nrOfInstances).props(props)

  def props = Props(classOf[HttpActor])

  val sdf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z").withLocale(Locale.ENGLISH)

  case class HttpRequest(url: String)
  case class HttpResponse(url: String, html: String, date: DateTime)
}

/**
  * Created by fabiofumarola on 08/02/15.
  */
class HttpActor extends Actor with ActorLogging with HttpExecutor {
   import context.dispatcher
   import it.dtk.HttpActor._

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
       //akka.actor.Status.Failure

   }

   override def postStop(): Unit = {
     shutdown()
   }
 }
