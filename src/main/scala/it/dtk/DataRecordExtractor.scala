package it.dtk

import akka.actor.Actor

object DataRecordExtractor {
  case class Extractrecords(url: String)
  
}

trait DataRecordExtractor extends Actor{

}