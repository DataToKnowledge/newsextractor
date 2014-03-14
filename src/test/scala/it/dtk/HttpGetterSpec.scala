package it.dtk

import java.util.Date
import scala.concurrent.duration._
import akka.actor.Props
import it.dtk.util.MySpec

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
      res.html.getClass should be(classOf[String])
      res.headerDate.getClass should be(classOf[Date])
    }

    "returns an empty result when it fetches a 404" in {

      getter ! Get("http://www.google.it/asd")

      val res = expectMsgClass(10.seconds, classOf[Fail])
      res.ex.getClass should be(classOf[GetException])
    }

    "returns an empty result when it goes in timeout" in {

      getter ! Get("http://www.go.it/")

      val res = expectMsgClass(classOf[Fail])
      res.ex.getClass should be(classOf[ConnectException])
    }

    "returns the body from the destination page when it fetches a 301 on the first page" in {

      getter ! Get("http://www.lecceprima.it/cronaca/pag/1/")

      val res = expectMsgClass(10.seconds, classOf[Result])
      res.html.getClass should be(classOf[String])
      assert(!res.html.isEmpty)
      res.headerDate.getClass should be(classOf[Date])
    }

  }

}
