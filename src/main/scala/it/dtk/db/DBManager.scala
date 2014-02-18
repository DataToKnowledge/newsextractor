package it.dtk.db

import akka.actor.Actor

object DBManager {
  case class Save(record: AnyRef)
  case object Done
  case object Fail
  case object Killed
}

/**
 * persist data in the db MongoDB indexed based on url
 * we should use Casbah http://mongodb.github.io/casbah/
 *
 * @author Michele Damiano Torelli <me@mdtorelli.it>
 */
class DBManager(host: String, port: Int, database: String) extends Actor {

  import DBManager._

  def receive = {
    case "hello" =>
      sender ! Done

    case "kill" =>
      context.stop(self)
      sender ! Killed

    case _ =>
      sender ! Fail
  }

}