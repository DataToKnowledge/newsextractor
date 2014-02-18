package it.dtk

import akka.testkit.TestKit
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender
import akka.actor.Props

object MainContenExtractorSpec {

  val rightHtml = ""

  val rightTitle = ""

  val rightCanonUrl = ""

}

class MainContentExtractorSpec extends TestKit(ActorSystem("MainContentExtractorSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  import MainContenExtractorSpec._

  "A MainContentExtractor Actor" must {
    "return the right main content" in {
      val mainContentActor = system.actorOf(Props(new MainContentExtractor(rightHtml)))
      //expectMsg(MainContentExtractor.Result(aContent))
    }
  }
}