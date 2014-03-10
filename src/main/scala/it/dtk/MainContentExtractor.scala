package it.dtk

import akka.actor.{ Props, Actor }
import com.gravity.goose.Goose
import com.gravity.goose.Configuration
import it.dtk.db.News
import akka.actor.actorRef2Scala
import scala.util.Success
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy

object MainContentExtractor {
  case class Result(news: News)
  case class Extract(news: News)
}

/**
 * @author fabiana
 *
 */
class MainContentExtractor(news: News) extends Actor {

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 5) {
    case _: Exception => SupervisorStrategy.Restart
  }

  import MainContentExtractor._

  val configuration = new Configuration()
  configuration.setImagemagickConvertPath("convert")
  configuration.setImagemagickIdentifyPath("identify")
//    configuration.setImagemagickConvertPath("/usr/local/bin/convert")
//  configuration.setImagemagickIdentifyPath("/usr/local/bin/identify")

  val goose = new Goose(configuration)

  context.watch(context.actorOf(Props(classOf[HttpGetter], news.urlNews.get)))

  val boilerPipe = None

  def receive = {
    case Success(HttpGetter.Result(url, html, date)) =>
      val article = goose.extractContent(url, html)

      if (article.cleanedArticleText.isEmpty()) {
        // TODO: Call BoilerPipe to extract the text (Issue #24)
        article.cleanedArticleText = ""
      }

      context.parent ! Result(news.copy(text = Some(article.cleanedArticleText), tags = Some(article.tags.toSet),
        metaDescription = Some(article.metaDescription), metaKeyword = Some(article.metaKeywords),
        canonicalUrl = Some(article.canonicalLink), topImage = Some(article.topImage.getImageSrc)))
      context.stop(self)
  }

}




