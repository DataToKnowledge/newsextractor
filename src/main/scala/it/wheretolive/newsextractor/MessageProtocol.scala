package it.wheretolive.newsextractor
import com.ning.http.client.Response
import it.wheretolive.newsextractor.DataModel._
import org.joda.time.DateTime

/**
 * Created by fabiofumarola on 08/02/15.
 */
object MessageProtocol {

  case class HttpRequest(url: String)
  case class HttpResponse(url: String, html: String, date: DateTime)

  case class ArticleRequest(url: String, html: String)
  case class ArticleResponse(resp: DataArticle)

  case class DataRecordRequest(url: String)
  case class DataRecordResponse(list: List[DataRecord])

  object Process

}
