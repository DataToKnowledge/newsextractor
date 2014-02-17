package it.dtk

import akka.actor.Actor
import java.util.Date
import com.ning.http.client.AsyncHttpClient
import it.dtk.HttpGetter.Result
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.concurrent.{TimeUnit}
import scala.concurrent.ExecutionException

object HttpGetter {

  case class Result(html: Option[String], headerDate: Option[Date])

}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 *         uses AsyncWebClient to fetch the web page
 *         should implement a strategy to recover errors
 *         and have a timeout for html retrieval
 */
class HttpGetter(url: String) extends Actor {

  private val client = new AsyncHttpClient
  private val sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")

  def receive = {
    case x =>
      val f = client.prepareGet(url).execute

      try {
        val response = f.get(10, TimeUnit.SECONDS)

        if (response.getStatusCode < 400)
          sender ! new Result(Some(response.getResponseBody), Some(sdf.parse(response.getHeader("Date"))))
        else
          throw new IOException(String.valueOf(response.getStatusCode))
      } catch {
        case _: ExecutionException | _: IOException => sender ! new Result(None, None)
      }
  }

}