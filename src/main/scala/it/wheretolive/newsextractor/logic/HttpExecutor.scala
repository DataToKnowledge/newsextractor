package it.wheretolive.newsextractor.logic

import java.util.concurrent.Executor

import com.ning.http.client.{AsyncHttpClient, AsyncHttpClientConfig, Response}

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._

case class BadStatus(url: String, status: Int) extends Throwable(s"HTTP status code: ${status.toString}")
case class GetException(url: String, innerException: Throwable) extends Throwable(innerException.getMessage)

/**
 * Created by fabiofumarola on 08/02/15.
 */
trait HttpExecutor {

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
        }
        catch {
          case t: Throwable =>
            p.failure(GetException(u, t))
        }
      }

    }, exec)
    p.future
  }

  def shutdown(): Unit = client.close()
}
