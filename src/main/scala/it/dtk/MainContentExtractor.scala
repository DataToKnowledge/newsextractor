package it.dtk

import akka.actor.Actor
import it.dtk.db.DataRecord


object MainContentExtractor {
  case class Result(record: DataRecord)
}


/**
 * @author fabiofumarola
 *
 */
class MainContentExtractor(html: String) extends Actor {

  def receive = ???
}