package it.dtk

import org.joda.time.{ DateTime, DateTimeZone }
import reactivemongo.bson.BSONObjectID

/**
 * Created by fabiofumarola on 06/02/15.
 */
object DataModel {
  type Url = String
  type TagName = String

  val zone = DateTimeZone.forID("Europe/Rome");
  DateTimeZone.setDefault(zone)

  case class CrawledWebSites(
    id: Option[BSONObjectID] = None,
    webSiteName: Option[String],
    urlWebSite: Option[String],
    controllerName: Option[String],
    enabled: Option[Boolean] = Option(false))

  case class CrawledNews(
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
      processing: Boolean = false,
      nlpAnalyzed: Boolean = false) {
    val extractionDate = DateTime.now
  }

  case class DataRecord(
    title: String,
    summary: String,
    newsUrl: String,
    newsDate: DateTime)

}
