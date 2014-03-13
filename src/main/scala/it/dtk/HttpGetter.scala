package it.dtk

import akka.actor.{ ActorLogging, Actor }
import java.util.{ Locale, Date }
import java.util.concurrent.Executor
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
import scala.concurrent.Promise
import com.ning.http.client.{AsyncHttpClientConfig, Response, AsyncHttpClient}
import scala.concurrent.Future

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

  case class Get(url: String)

}

/**
 * Fetches the HTML of a given Web page.
 *
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class HttpGetter extends Actor with ActorLogging {

  import HttpGetter._

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  private val sdf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z").withLocale(Locale.ENGLISH)

  /**
   * When receives any message it replies with the HTML of the given Web page
   */
  override def receive: Actor.Receive = {

    case Get(url) =>
      val send = sender

      AsyncWebClient.get(url) map { res =>
        send ! new Result(url, res.getResponseBody, sdf.parseDateTime(res.getHeader("Date")).toDate)
      } recover {
        case ex: GetException =>
          send ! ex
      }
  }
}

case class BadStatus(status: Int) extends RuntimeException

object AsyncWebClient {
  private val config = new AsyncHttpClientConfig.Builder();
  config.setFollowRedirects(true);
  private val client = new AsyncHttpClient(config.build())

  def get(url: String)(implicit exec: Executor): Future[Response] = {
    val f = client.prepareGet(url).execute()
    val p = Promise[Response]()
    f.addListener(new Runnable {
      def run() = {
        val response = f.get()
        if (response.getStatusCode < 400)
          p.success(response)
        else
          p.failure(BadStatus(response.getStatusCode))
      }
    }, exec)
    p.future
  }

  def shutdown() = client.close()
}

