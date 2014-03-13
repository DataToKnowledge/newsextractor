package it.dtk

import akka.actor.{ Props, Actor }
import com.gravity.goose.Goose
import com.gravity.goose.Configuration
import it.dtk.db.News
import akka.actor.actorRef2Scala
import scala.util.Success
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import de.l3s.boilerpipe.extractors
import de.l3s.boilerpipe.extractors.ArticleExtractor
import com.gravity.goose.Article
import scala.util.Try
import com.gravity.goose.network.ImageFetchException
import scala.util.Failure
import scala.util.Failure
import akka.actor.ActorRef
import akka.actor.ActorLogging

object MainContentExtractor {
  case class Result(news: News)
  case class Extract(news: News)
}

/**
 * @author fabiana
 *
 */
class MainContentExtractor(news: News, routerHttpGetter: ActorRef) extends Actor with ActorLogging{

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 5) {
    case _: Exception => SupervisorStrategy.Restart
  }

  import MainContentExtractor._

  val configuration = new Configuration()
  configuration.setImagemagickConvertPath("convert")
  configuration.setImagemagickIdentifyPath("identify")
  configuration.enableImageFetching = false
  val goose = new Goose(configuration)

  routerHttpGetter ! HttpGetter.Get(news.urlNews.get)

  def receive = {
    case HttpGetter.Result(url, html, date) =>

      val tryArticle = Try[Article] {
        goose.extractContent(url, html)
      } recover {
        case ex: ImageFetchException =>
          val conf = new Configuration()
          conf.setEnableImageFetching(x$1 = false)
          val gooseWithoutImage = new Goose(conf)
          gooseWithoutImage.extractContent(url, html)
      }

      tryArticle match {
        case Success(article) =>
          if (article.cleanedArticleText.isEmpty) {
            val extractor = ArticleExtractor.getInstance()
            article.cleanedArticleText = extractor.getText(html)
          }

          context.parent ! Result(news.copy(text = Some(article.cleanedArticleText), tags = Some(article.tags.toSet),
            metaDescription = Some(article.metaDescription), metaKeyword = Some(article.metaKeywords),
            canonicalUrl = Some(article.canonicalLink), topImage = Some(article.topImage.getImageSrc)))
          context.stop(self)

        case Failure(ex) =>
          throw ex
      }

    case GetException(url, statusCode) =>
      log.error("Failed to get the HTML for URL {} with status code {}", url, statusCode)

  }

}




