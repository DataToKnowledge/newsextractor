package it.wheretolive.newsextractor

import javax.management.monitor.StringMonitor

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
    websiteName: Option[String],
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

  case class DataArticle(
    canonicalUrl: Option[String],
    metaDescription: Option[String],
    metaKeywords: Option[String],
    tags: Option[Set[String]],
    corpus: Option[String],
    topImage: Option[String])

}
