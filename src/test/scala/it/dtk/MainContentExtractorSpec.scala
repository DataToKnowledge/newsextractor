package it.dtk

import akka.testkit.TestKit
import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import akka.actor.Props
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }
import it.dtk.util.StepParent

class MainContentExtractorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  import MainContentExtractor._

  def this() = this(ActorSystem("MainContentExtractorSpec"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A MainContentExtractor Actor" must {

    "return the right main content" in {
      system.actorOf(Props(new StepParent(Props(new MainContentExtractor(rightCanonUrl, lines)), testActor)), "rightContent")

      val res = expectMsgClass(classOf[Result])
      res.record.title should contain (rightTitle)
      res.record.extractionDate should not be null

    }

  }

  val rightTitle = "Giovani, anziani, asili nido e soldi per il Sud ecco il progetto del governo per l'equità"
  val rightCanonUrl = "http://www.repubblica.it/economia/2012/05/12/news/giovani_anziani_asili_nido_e_soldi_per_il_sud_ecco_il_progetto_del_governo_per_l_equit-34962952/"
  val lines = scala.io.Source.fromFile("test.txt", "utf-8").getLines().mkString
}