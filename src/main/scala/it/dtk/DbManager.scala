package it.dtk

import akka.actor.Actor

object DbManager {
  case class Save(record: AnyRef)
  case object Done
  case object Fail
}

/**
 * @author Daniele & Fabio
 * persist data in the db MongoDB indexed based on url
 * we should use Casbah http://mongodb.github.io/casbah/
 */
class DbManager extends Actor{

  def receive = ???
}