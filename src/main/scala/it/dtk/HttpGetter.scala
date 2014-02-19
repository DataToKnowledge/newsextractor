package it.dtk

import akka.actor.{ActorLogging, Actor}
import akka.pattern.pipe
import java.util.{Locale, Date}
import java.util.concurrent.Executor
import com.ning.http.client.providers.netty.NettyResponse
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
import it.dtk.HttpGetter.{PageNotFoundException, Result}
import scala.util.{Success, Failure}

object HttpGetter {

  /**
   * Wraps the HTML and the Web page last modified time.
   *
   * @param url the given Web page url
   * @param html fetched HTML
   * @param headerDate time extracted from response headers
   */
  case class Result(url: String, html: String, headerDate: Date)

  case class PageNotFoundException() extends Throwable

}

/**
 * Fetches the HTML of a given Web page.
 *
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class HttpGetter(url: String) extends Actor with ActorLogging {

  import dispatch._

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  private val sdf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z")
  sdf.withLocale(Locale.ENGLISH)

  Http(dispatch.url(url)).either pipeTo self

  /**
   * When receives any message it replies with the HTML of the given Web page
   */
  override def receive: Actor.Receive = {
    case Right(res: NettyResponse) =>
      if (res.getStatusCode < 400) {
        log.info("Successufully got the HTML")
        context.parent ! Success(new Result(url, res.getResponseBody, sdf.parseDateTime(res.getHeader("Date")).toDate))
      } else
        context.parent ! Failure(new PageNotFoundException)
    case Left(error: Throwable) =>
      context.parent ! Failure(error)
  }
}
