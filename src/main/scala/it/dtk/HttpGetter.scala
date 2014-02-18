package it.dtk

import akka.actor.Actor
import akka.pattern.pipe
import java.util.{Locale, Date}
import java.util.concurrent.Executor
import com.ning.http.client.providers.netty.NettyResponse
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
import it.dtk.HttpGetter.Result

object HttpGetter {

  /**
   * Wraps the HTML and the Web page last modified time.
   *
   * @param html fetched HTML
   * @param headerDate time extracted from response headers
   */
  case class Result(html: Option[String], headerDate: Option[Date])

}

/**
 * Fetches the HTML of a given Web page.
 *
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class HttpGetter(url: String) extends Actor {

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
      if (res.getStatusCode < 400)
        context.parent ! new Result(Some(res.getResponseBody), Some(sdf.parseDateTime(res.getHeader("Date")).toDate))
      else
        context.parent ! new Result(None, None)
    case Left(error) =>
      context.parent ! new Result(None, None)
  }
}
