package it.dtk

import akka.testkit.TestKit
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender
import akka.actor.Props

object RepubblicaWebSiteControllerSpec {
  
}

class RepubblicaWebSiteControllerSpec extends TestKit(ActorSystem("MainContentExtractorSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  import RepubblicaWebSiteControllerSpec._
  
  //val repubblicaController = system.actorOf(props)
  
  "The Repubblica Controller" must {
    "start when the start message is send" in {
      
    }
  }
}