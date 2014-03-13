package it.dtk

import akka.actor.{ ActorLogging, Actor }
import java.util.{ Locale, Date }
import java.util.concurrent.Executor
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
import scala.concurrent.Promise
import com.ning.http.client.{ AsyncHttpClientConfig, Response, AsyncHttpClient }
import scala.concurrent.Future
import scala.concurrent.duration._
import com.ning.http.client.AsyncCompletionHandler
import scala.util.Success
import scala.util.Failure

object HttpGetter {

  /**
   * Wraps the HTML and the Web page last modified time.
   *
   * @param url the given Web page url
   * @param html fetched HTML
   * @param headerDate time extracted from response headers
   */
  case class Result(url: String, html: String, headerDate: Date)

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
      val u = url
      log.info("getting the html for the url {}", url)
      context.system.scheduler.scheduleOnce(1.second) {
        val future = AsyncWebClient.get(url)
        future.onComplete {
          case Success(res) =>
            send ! new Result(url, res.getResponseBody, sdf.parseDateTime(res.getHeader("Date")).toDate)
          case Failure(ex) =>
            send ! Fail(u,ex)
        }
      }
  }
  override def postStop = {
    AsyncWebClient.shutdown
  }
}

object AsyncWebClient {

  private val client = init

  def init(): AsyncHttpClient = {

    val builder = new AsyncHttpClientConfig.Builder()
    builder.setFollowRedirects(true)
    builder.setMaximumConnectionsPerHost(2)
    builder.setAllowPoolingConnection(false)
    new AsyncHttpClient(builder.build())
  }

  def get(url: String): Future[Response] = {
    val p = Promise[Response]()
    
    val f = client.prepareGet(url).execute(new AsyncCompletionHandler[Response] {

      override def onCompleted(response: Response): Response = {
        p.success(response)
        response
      }

      override def onThrowable(t: Throwable) = {
        p.failure(t)
      }
    })
    p.future
  }

  def shutdown() = client.close()
}

