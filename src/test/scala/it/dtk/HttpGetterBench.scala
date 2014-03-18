package it.dtk

import scala.concurrent.duration._
import akka.actor.Props
import it.dtk.util.MySpec
import org.joda.time.{Period, DateTime}
import org.joda.time.format.PeriodFormatterBuilder

/**
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class HttpGetterBench extends MySpec("HttpGetterBench") {

  import HttpGetter._

  "An HttpGetter actor" must {

    val getter = system.actorOf(Props[HttpGetter])

    "get all pages without errors" in {
      val startTime = DateTime.now
      var errorsNum = 0

      urls.view.zipWithIndex foreach {
        case (url, index) =>
          print(s"- Getting page #${index + 1} of ${urls.size} ...")

          getter ! Get(url)

          val res = expectMsgAnyClassOf(10.seconds, classOf[Any])

          res match {
            case res: Result =>
              res.html.getClass should be(classOf[String])
              res.headerDate.getClass should be(classOf[DateTime])
              println("SUCCESS!")

            case res: Fail =>
              println("FAIL!")
              println(s"URL: ${res.url}")
              println(s"Exception message: ${res.ex.getMessage}")
              errorsNum += 1
          }
      }

      val stopTime = DateTime.now
      val diff = new Period(startTime, stopTime)
      val ms = new PeriodFormatterBuilder().minimumPrintedDigits(1).appendMinutes().appendSuffix(" minutes and ").appendSeconds().appendSuffix(" seconds").toFormatter

      println(s"\nTested ${urls.size} URLs in ${ms print diff} with $errorsNum errors")
      if (errorsNum > 0) fail()
    }
  }

  val urls = List(
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9956%3Awta-indian-wells--trionfo-di-flavia-pennetta&catid=43%3Aslideshow&itemid=83&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9951%3Afrancavilla-fontana-raccolta-firme-per-la-lista-laltra-europa-con-tsipras&catid=55%3Apolitica&itemid=68&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9935%3A-tennis-flavia-pennetta-vola-in-finale&catid=43%3Aslideshow&itemid=83&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9930%3Aalla-manovella-martedi-18-marzo-alle-ore-1830-si-presenta-lintrigante-corso-di-fotofilosofia&catid=48%3Acultura&itemid=77&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9953%3Alsu-17-e-18-marzo-proteste&catid=55%3Apolitica&itemid=68&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9963%3Abrindisi-oltre-mezzo-milione-di-premi-per-i-dipendenti-multiservizi-il-sindaco-taglia-tutto&catid=55%3Apolitica&itemid=68&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9945%3Atuturano-circola-con-una-falsa-assicurazione-denunciato&catid=41%3Acronaca&itemid=75&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9961%3Aon-mariano-la-vittoria-di-flavia-pennetta-non-puo-che-riempirci-di-orgoglio-e-soddisfazione&catid=55%3Apolitica&itemid=68&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9944%3Aoria-unautovettura-a-fuoco-nella-notte&catid=41%3Acronaca&itemid=75&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9934%3Amagno-il-consiglio-sappia-essere-unitariamente-protagonista-del-nostro-futuro&catid=49%3Aambiente&itemid=79&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9938%3A-peter-pan-l-ultimo-appuntamento-per-il-posto-delle-favole&catid=52%3Aspettacolo&itemid=84&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9957%3Ail-regista-mirko-locatelli-al-cineporto-di-bari-per-la-proiezione-del-suo-film-i-corpi-estranei-ingresso-libero-fino-a-esaurimento-posti&catid=52%3Aspettacolo&itemid=84&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9932%3Abrindisi-trasporto-durgenza-con-un-falcon-50&catid=50%3Aapprofondimenti&itemid=81&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9940%3Acarovigno-ordinanza-di-carcerazione-per-evasione&catid=41%3Acronaca&itemid=75&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9954%3Atoni-matarrelli-deputato-della-repubblica-flavia-pennetta-conferma-di-essere-uno-dei-piu-valorosi-portabandiera-dello-sport-italiano-nel-mondo&catid=55%3Apolitica&itemid=68&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9952%3Arinascita-civica-brindisina-e-friends-bike-ancora-insieme-per-l-orgoglio-ciclistico-brindisino&catid=54%3Asport&itemid=69&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9947%3Aeditoriale-connubio-ambiente-occupazione-nel-consiglio-monotematico-brindisino&catid=43%3Aslideshow&itemid=83&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9942%3Aforte-denuncia-su-facebook-del-segretario-provinciale-della-flai-cgil-angelo-leo-su-un-lavoratore-agricolo-africano-minacciato-di-morte&catid=50%3Aapprofondimenti&itemid=81&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9931%3Apreghiera-taize-e-cineforum-a-sostegno-della-legalita&catid=48%3Acultura&itemid=77&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9962%3Alecce-ex-deposito-carburanti-apisem-nessun-inquinante-nelle-al-parco-di-belloluogo&catid=55%3Apolitica&itemid=68&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9939%3Asan-vito-dei-normanni-45enne-arrestato-per-maltrattamenti-e-violenza-sessuale&catid=41%3Acronaca&itemid=75&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9949%3Amoda-il-lavoro-della-modella-o-indossatrice&catid=50%3Aapprofondimenti&itemid=81&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9955%3Abrindisi-in-cammino-verso-la-giornata-della-memoria-e-dellimpegno-di-latina&catid=55%3Apolitica&itemid=68&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9941%3Asan-pietro-vernotico-evade-dai-domiciliari-arrestato&catid=41%3Acronaca&itemid=75&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9946%3Atrio-basket-brindisi-ora-la-salvezza-e-matematica-&catid=54%3Asport&itemid=69&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9937%3Ahai-fifa-il-mega-torneo-di-fifa-14-su-schermi-giganti&catid=52%3Aspettacolo&itemid=84&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9936%3Ala-segretaria-nazionale-della-cgil-camusso---con-il-dl-poletti-sul-lavoro-piu-precarieta-&catid=55%3Apolitica&itemid=68&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9959%3Arapporto-almalaurea-2014-laureati-delluniversita-di-bari-e-il-lavoro&catid=50%3Aapprofondimenti&itemid=81&view=article",
    "http://www.brindisilibera.it/new/index.php?option=com_content&id=9933%3Ail-regista-mirko-locatelli-al-cineporto-di-bari-per-la-proiezione-del-suo-film-i-corpi-estranei-ingresso-libero-fino-a-esaurimento-posti&catid=52%3Aspettacolo&itemid=84&view=article",
    "http://corrieredelmezzogiorno.corriere.it/bari/notizie/cronaca/2012/24-maggio-2012/tenta-abusare-un-13enne-rom-viene-visto-un-agente-fuori-servizio-201316934664.shtml"
  )
}
