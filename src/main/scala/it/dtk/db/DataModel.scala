package it.dtk.db

import java.util.Date
import org.joda.time.DateTime
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDateTime

object DataModel {
  type Url = String
  type TagName = String
}

case class WebControllerData(id: Option[BSONObjectID] = None, controllerName: Option[String], 
    stopUrls: Option[List[String]], enabled: Option[Boolean] = Option(false))

/**
 * @author fabiofumarola
 * @param id it is the progressive counter of the news extracted by the actor. the counter is reset to 0 every day
 */
case class News(id: Option[BSONObjectID] = None, urlWebSite: Option[String], urlNews: Option[String],
                title: Option[String], summary: Option[String], newsDate: Option[DateTime],
                text: Option[String] = None, tags: Option[Set[String]] = None,
                metaDescription: Option[String] = None, metaKeyword: Option[String] = None,
                canonicalUrl: Option[String] = None, topImage: Option[String] = None, nlpAnalyzed: Option[Boolean]) {

  val extractionDate: Date = (new DateTime).toDate
}

object News {

  implicit object NewsBSONReader extends BSONDocumentReader[News] {

    def read(doc: BSONDocument): News = {
      News(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("urlWebSite"),
        doc.getAs[String]("urlNews"),
        doc.getAs[String]("title"),
        doc.getAs[String]("summary"),
        doc.getAs[BSONDateTime]("newsDate").map(dt => new DateTime(dt.value)),
        doc.getAs[String]("text"),
        doc.getAs[Set[String]]("tags"),
        doc.getAs[String]("metaDescription"),
        doc.getAs[String]("metaKeyword"),
        doc.getAs[String]("canonicalUrl"),
        doc.getAs[String]("topImage"),
        doc.getAs[Boolean]("nlpAnalyzed"))
    }
  }

  implicit object NewsBSONWriter extends BSONDocumentWriter[News] {
    def write(news: News): BSONDocument = BSONDocument(
      "urlWebSite" -> news.urlWebSite,
      "urlNews" -> news.urlNews,
      "title" -> news.title,
      "summary" -> news.summary,
      "newsDate" -> news.newsDate.map(t => BSONDateTime(t.getMillis)),
      "text" -> news.text,
      "tags" -> news.tags,
      "metadescription" -> news.metaDescription,
      "metakeyword" -> news.metaKeyword,
      "canonicalUrl" -> news.canonicalUrl,
      "topImage" -> news.topImage,
      "nlpAnalyzed" -> Option(false)
    )

  }

}

object WebControllerData {

  implicit object WebControllerDataBSONReader extends BSONDocumentReader[WebControllerData] {
    def read(doc: BSONDocument): WebControllerData = {
      WebControllerData(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("controllerName"),
        doc.getAs[List[String]]("stopUrls"),
        doc.getAs[Boolean]("enabled")
      )
    }
  }

  implicit object WebControllerDataBSONWriter extends BSONDocumentWriter[WebControllerData] {
    def write(webController: WebControllerData): BSONDocument = BSONDocument(
      "controllerName" -> webController.controllerName,
      "stopUrls" -> webController.stopUrls,
      "enabled" -> webController.enabled
    )
  }

}