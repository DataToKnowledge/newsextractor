package it.dtk

import akka.actor.Actor
import it.dtk.model.DataRecord
import akka.actor.ActorLogging

object DataRecordExtractor {
  case class Extract(url: String)
  case class ExtractedDataRecords(url: String, records: Vector[DataRecord])
}

/**
 * @author 
 * 
 *
 */
class DataRecordExtractor(url: String) extends Actor with ActorLogging{
  import DataRecordExtractor._ 
  
  def receive = ???
}