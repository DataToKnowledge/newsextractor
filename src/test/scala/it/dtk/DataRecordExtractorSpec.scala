package it.dtk

import akka.testkit.TestKit
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender
import it.dtk.db.DataRecord

object DataRecordExtractorSpec {
  
  val righDataRecords: Vector[DataRecord] = Vector()
  
  val inputUrl: String = ""
  
}

class DataRecordExtractorSpec extends TestKit(ActorSystem("GetterSpec")) 
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  "A Data Record Extractor" must {
    
    "return the right data records" in {
      
    }
  }
}