package it.dtk

import akka.actor.{ ActorLogging, Actor }
import java.util.Locale
import java.util.concurrent.Executor
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
import scala.concurrent.Promise
import com.ning.http.client.{ AsyncHttpClientConfig, Response, AsyncHttpClient }
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Success
import org.joda.time.DateTime
import scala.util.Failure

object HttpGetter {

  /**
   * Wraps the HTML and the Web page last modified time.
   *
   * @param url the given Web page url
   * @param html fetched HTML
   * @param headerDate time extracted from response headers
   */
  case class Result(url: String, html: String, headerDate: DateTime)

  case class Get(url: String)

  case class Fail(url: String, ex: Throwable)

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
  override def receive = {

    case Get(url) =>
      val send = sender
      //log.info("Getting the HTML for the URL {}", url)
      val future = AsyncWebClient.get(url)
      future.onComplete {
        case Success(res) =>
          val date = if (res.getHeader("Date") != null) sdf.parseDateTime(res.getHeader("Date")) else DateTime.now()
          send ! new Result(url, res.getResponseBody, date)
        case Failure(ex) =>
          send ! Fail(url, ex)
      }
  }

  override def postStop() = {
    AsyncWebClient.shutdown()
  }
}

case class BadStatus(url: String, status: Int) extends Throwable(s"HTTP status code: ${status.toString}")
case class GetException(url: String, innerException: Throwable) extends Throwable(innerException.getMessage)

object AsyncWebClient {
  val builder = new AsyncHttpClientConfig.Builder()
  builder.setFollowRedirects(true)
  builder.setCompressionEnabled(false)
  builder.setConnectionTimeoutInMs(240.seconds.toMillis.toInt)
  builder.setRequestTimeoutInMs(240.seconds.toMillis.toInt)
  //builder.setMaximumConnectionsPerHost(2)
  builder.setAllowPoolingConnection(true)

  private val client = new AsyncHttpClient(builder.build())

  def get(url: String)(implicit exec: Executor): Future[Response] = {
    val u = url
    val f = client.prepareGet(url).execute()
    val p = Promise[Response]()
    f.addListener(new Runnable {
      def run() = {
        try {
          val response = f.get()
          if (response.getStatusCode < 400)
            p.success(response)
          else p.failure(BadStatus(u, response.getStatusCode))
        } catch {
          case t: Throwable =>
            p.failure(GetException(u,t))
        }
      }

    }, exec)
    p.future
  }

  def shutdown(): Unit = client.close()
}