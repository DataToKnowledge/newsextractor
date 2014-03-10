package it.dtk

import akka.actor.{ ActorLogging, Actor }
import akka.pattern.pipe
import java.util.{ Locale, Date }
import java.util.concurrent.Executor
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
import it.dtk.HttpGetter.{ DispatchException, GetException, Result }
import scala.util.{ Success, Failure }
import com.ning.http.client.providers.netty.NettyResponse
import org.jsoup.safety.{ Whitelist, Cleaner }
import org.jsoup.nodes.Document
import org.jsoup.Jsoup

object HttpGetter {

  /**
   * Wraps the HTML and the Web page last modified time.
   *
   * @param url the given Web page url
   * @param html fetched HTML
   * @param headerDate time extracted from response headers
   */
  case class Result(url: String, html: String, headerDate: Date)

  case class GetException(url: String, statusCode: Int) extends Throwable

  case class DispatchException(url: String, error: Throwable) extends Throwable

}

/**
 * Fetches the HTML of a given Web page.
 *
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class HttpGetter(url: String) extends Actor with ActorLogging {

  import dispatch._

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  private val sdf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z").withLocale(Locale.ENGLISH)

  Http.configure(_ setFollowRedirects true)(dispatch.url(url)).either pipeTo self

  /**
   * When receives any message it replies with the HTML of the given Web page
   */
  override def receive: Actor.Receive = {
    case Right(res: NettyResponse) =>
      val statusCode = res.getStatusCode
      if (statusCode < 400) {
        //val html = new Cleaner(Whitelist.relaxed()).clean(Jsoup.parse(res.getResponseBody)).html
        context.parent ! Success(new Result(url, res.getResponseBody, sdf.parseDateTime(res.getHeader("Date")).toDate))
        //context.parent ! Success(new Result(url, Jsoup.parseBodyFragment(html).body().html, sdf.parseDateTime(res.getHeader("Date")).toDate))
        context.stop(self)
      } else {
        context.parent ! Failure(new GetException(url, statusCode))
        context.stop(self)
      }
    case Left(error: Throwable) =>
      context.parent ! Failure(new DispatchException(url, error))
      context.stop(self)
  }
}
