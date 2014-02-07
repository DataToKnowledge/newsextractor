package it.dtk

import akka.actor.Actor

object MainContentExtractor {
  case class Extract(record: AnyRef)
}


/**
 * @author fabiofumarola
 *
 */
class MainContentExtractor extends Actor {

  def receive = ???
}