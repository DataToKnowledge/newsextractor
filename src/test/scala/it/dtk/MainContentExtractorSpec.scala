package it.dtk

import it.dtk.util.{StepParent, MySpec}

class MainContentExtractorSpec extends MySpec("MainContentExtractorSpec") {

  import MainContentExtractor._

  /*
  "A MainContentExtractor Actor" must {

    "return the right main content" in {
      system.actorOf(Props(new StepParent(Props(new MainContentExtractor(rightCanonUrl), testActor)), "rightContent")

      val res = expectMsgClass(10.seconds, classOf[Result])
      res.record.title.trim should be equals rightTitle
      res.record.extractionDate should not be null
      res.record.topImage should not be null
    }

  }
*/
  val rightTitle = "Giovani, anziani, asili nido e soldi per il Sud ecco il progetto del governo per l'equità"
  val rightCanonUrl = "http://www.repubblica.it/economia/2012/05/12/news/giovani_anziani_asili_nido_e_soldi_per_il_sud_ecco_il_progetto_del_governo_per_l_equit-34962952/"
  val lines = scala.io.Source.fromFile("src/test/resources/TestArticle.html", "utf-8").getLines().mkString

}