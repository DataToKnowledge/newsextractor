package it.dtk

import akka.actor.{Props, Actor}
import com.gravity.goose.Goose
import com.gravity.goose.Configuration
import it.dtk.db.News
import scala.util.Success

object MainContentExtractor {

  case class Result(news: News)

}

/**
 * @author fabiofumarola
 *
 */
class MainContentExtractor(news: News) extends Actor {


  context.watch(context.actorOf(Props(classOf[HttpGetter], news.urlNews)))

  val configuration = new Configuration()
  configuration.setImagemagickConvertPath("convert")
  configuration.setImagemagickIdentifyPath("identify")

  val goose = new Goose(configuration)

  def receive = {
    case Success(HttpGetter.Result(url, html, date)) =>
      val article = goose.extractContent(url, html)
      context.parent ! news.copy(text = Some(article.cleanedArticleText), tags = Some(article.tags.toSet),
        metaDescription = Some(article.metaDescription), metaKeyword = Some(article.metaKeywords),
        canonicalUrl = Some(article.canonicalLink), topImage = Some(article.topImage.getImageSrc))
  }


}