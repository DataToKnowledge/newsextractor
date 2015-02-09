package it.wheretolive.newsextractor.logic

import com.gravity.goose.network.ImageFetchException
import com.gravity.goose.{ Configuration, Goose }
import it.wheretolive.newsextractor.DataModel.DataArticle

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by fabiofumarola on 27/01/15.
 */
trait GoseArticleExtractor {

  private val configuration = new Configuration()
  configuration.setImagemagickConvertPath("convert")
  configuration.setImagemagickIdentifyPath("identify")
  configuration.enableImageFetching = true
  private val goose = new Goose(configuration)

  def extract(url: String, html: String)(implicit executor: ExecutionContext): Future[DataArticle] =
    Future {
      goose.extractContent(url, html)
    }.recover {
      case ex: ImageFetchException =>
        val conf = new Configuration()
        conf.setEnableImageFetching(false)
        val gooseWithoutImage = new Goose(conf)
        gooseWithoutImage.extractContent(url, html)
    }.map { a =>

      DataArticle(
        canonicalUrl = Option(a.canonicalLink),
        metaDescription = Option(a.metaDescription),
        metaKeywords = Option(a.metaKeywords),
        tags = Option(a.tags.toSet),
        corpus = Option(a.cleanedArticleText),
        topImage = Option(a.topImage.imageSrc)
      )
    }
}
