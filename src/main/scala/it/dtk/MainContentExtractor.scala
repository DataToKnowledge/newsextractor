package it.dtk

import akka.actor.Actor

object MainContentExtractor {
  case class Extract(record: AnyRef)
}


class MainContentExtractor extends Actor {

}