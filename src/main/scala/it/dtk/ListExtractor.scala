package it.dtk

import akka.actor.Actor

object ListExtractor {
  case class ExtractList(url: String)
}

/**
 * @author Andrea & Fabiana
 * 
 */
class ListExtractor extends Actor{

  def receive = ???
}