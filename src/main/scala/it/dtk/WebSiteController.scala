package it.dtk

import akka.actor.Actor

object WebSiteController {
  case class Extract(url: String)
  case object Done
  case object Fail
  case class Check(record: AnyRef)
  case class Save(record: AnyRef)
}

trait WebSiteController extends Actor {
}

