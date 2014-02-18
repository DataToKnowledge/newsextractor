package it.dtk

import akka.actor.Actor

object DataRecordExtractor {
  case class ExtractedRecords(url: String, dataRecords: List[String])
  
}

trait DataRecordExtractor extends Actor{

}