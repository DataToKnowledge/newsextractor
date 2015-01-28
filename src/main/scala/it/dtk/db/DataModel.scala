package it.dtk.db

import java.util.Date
import it.dtk.db.DataModel.{ CrawledWebSites, FetchedNews }
import org.joda.time.DateTime
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDateTime

object DataModel {
  type Url = String
  type TagName = String

  case class CrawledWebSites(
    id: Option[BSONObjectID] = None,
    controllerName: Option[String],
    stopUrls: Option[List[String]],
    enabled: Option[Boolean] = Option(false))

  case class FetchedNews(
      id: Option[String] = None,
      urlWebSite: String,
      urlNews: String,
      title: String,
      summary: String,
      newsDate: Option[DateTime],
      corpus: String,
      tags: Set[String],
      metaDescription: String,
      metaKeyword: String,
      canonicalUrl: String,
      topImage: Option[String],
      nlpAnalyzed: Boolean = false) {
    val extractionDate = DateTime.now
  }

  case class DataRecord(
    title: String,
    summary: String,
    newsUrl: String,
    newsDate: DateTime)
}
