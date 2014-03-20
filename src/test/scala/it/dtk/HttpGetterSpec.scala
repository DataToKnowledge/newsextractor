package it.dtk

import scala.concurrent.duration._
import akka.actor.Props
import it.dtk.util.MySpec
import org.joda.time.DateTime

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class HttpGetterSpec extends MySpec("HttpGetterSpec") {

  import HttpGetter._

  "An HttpGetter actor" must {

    val getter = system.actorOf(Props[HttpGetter])

    "returns the body in a page" in {

      getter ! Get("http://www.google.it/")

      val res = expectMsgClass(10.seconds, classOf[Result])
      res.html shouldBe a [String]
      res.html should not be empty
      res.headerDate shouldBe a [DateTime]
    }

    "returns an empty result when it fetches a 404" in {

      getter ! Get("http://www.google.it/asd")

      val res = expectMsgClass(10.seconds, classOf[Fail])
      res.ex shouldBe a [BadStatus]
    }

    "returns an empty result when it goes in timeout" in {

      getter ! Get("http://www.go.it/")

      val res = expectMsgClass(classOf[Fail])
      res.ex shouldBe a [GetException]
    }

    "returns the body from the destination page when it fetches a 301 on the first page" in {

      getter ! Get("http://www.lecceprima.it/cronaca/pag/1/")

      val res = expectMsgClass(10.seconds, classOf[Result])
      res.html shouldBe a [String]
      res.html should not be empty
      res.headerDate shouldBe a [DateTime]
    }

  }

}
