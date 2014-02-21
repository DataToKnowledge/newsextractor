package it.dtk

import akka.testkit.TestKit
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender
import it.dtk.db.News

object DataRecordExtractorSpec {
  
  val righDataRecords: Vector[News] = Vector()
  
  val inputUrl: String = ""
  
}

class DataRecordExtractorSpec extends TestKit(ActorSystem("GetterSpec")) 
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  "A Data Record Extractor" must {
    
    "return the right data records" in {
      
    }
  }
}