package it.dtk.http

import akka.actor.Actor
import java.util.Date
import com.ning.http.client.AsyncHttpClient
import java.io.IOException
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionException
import it.dtk.http.HttpGetter.Result
import org.joda.time.format.DateTimeFormat

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

  private val client = new AsyncHttpClient
  private val sdf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z")

  /**
   * When receives any message it replies with the HTML of the given Web page
   */
  def receive = {
    case _ =>
      val f = client.prepareGet(url).execute

      try {
        val response = f.get(10, TimeUnit.SECONDS)

        if (response.getStatusCode < 400)
          sender ! new Result(Some(response.getResponseBody), Some(sdf.parseDateTime(response.getHeader("Date")).toDate))
        else
          throw new IOException(String.valueOf(response.getStatusCode))
      } catch {
        case _: ExecutionException | _: IOException => sender ! new Result(None, None)
      }
  }

}
