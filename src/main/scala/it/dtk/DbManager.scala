package it.dtk

import akka.actor.Actor

object DbManager {
  case class Save(record: AnyRef)
  case object Done
  case object Fail
}

/**
 * @author 
 * persist data in the db MongoDB indexed based on url
 */
class DbManager extends Actor{

  def receive = ???
}