package it.dtk.logic

import com.gravity.goose.network.ImageFetchException
import com.gravity.goose.{ Article, Goose, Configuration }
import it.dtk.db.DataModel.CrawledNews

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * Created by fabiofumarola on 27/01/15.
 */
trait GoseArticleExtractor {

  private val configuration = new Configuration()
  configuration.setImagemagickConvertPath("convert")
  configuration.setImagemagickIdentifyPath("identify")
  configuration.enableImageFetching = true
  private val goose = new Goose(configuration)

  def extract(url: String, html: String)(implicit executor: ExecutionContext) =
    Future {
      goose.extractContent(url, html)
    }.recover {
      case ex: ImageFetchException =>
        val conf = new Configuration()
        conf.setEnableImageFetching(false)
        val gooseWithoutImage = new Goose(conf)
        gooseWithoutImage.extractContent(url, html)
    }
}
