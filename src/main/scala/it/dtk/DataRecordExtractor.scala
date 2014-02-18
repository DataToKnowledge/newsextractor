package it.dtk

import akka.actor.Actor
import it.dtk.db.DataRecord

object DataRecordExtractor {
  case class ExtractedRecords(url: String, dataRecords: List[DataRecord])
  
}

trait DataRecordExtractor extends Actor{

}