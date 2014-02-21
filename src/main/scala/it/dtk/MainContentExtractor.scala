package it.dtk

import akka.actor.Actor
import it.dtk.db.DataRecord
import com.gravity.goose.Goose
import com.gravity.goose.Configuration
import it.dtk.db.DataRecord

object MainContentExtractor {
  case class Result(record: DataRecord)
}

/**
 * @author fabiofumarola
 *
 */
class MainContentExtractor(url: String, html: String) extends Actor {

  import MainContentExtractor._
  import it.dtk.db.DataRecord

  val goose = new Goose(new Configuration)
  val article = goose.extractContent(url, html)
  val dataRecord = DataRecord(id = -1, urlWebSite = article.domain, urlNews=article.canonicalLink, title=article.title, summary=article.metaDescription, newsDate= article.publishDate, //extractionDate=null, 
      tags= article.tags.toSet , metaDescription= article.metaDescription,metaKeyword= article.metaKeywords, text= article.cleanedArticleText, canonicalUrl=article.canonicalLink, topImage=article.topImage.getImageSrc)

  context.parent ! Result(dataRecord)

  def receive = {
    case _ => 
  }

 
}