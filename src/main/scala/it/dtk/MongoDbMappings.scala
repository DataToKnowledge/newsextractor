package it.dtk

import org.joda.time.DateTime
import reactivemongo.bson._
import DataModel._

/**
 * Created by fabiofumarola on 27/01/15.
 */
trait MongoDbMappings {

  implicit object NewsBSONWriter extends BSONDocumentWriter[CrawledNews] {

    override def write(news: CrawledNews): BSONDocument = {

      val date = news.newsDate.getOrElse(news.extractionDate)

      BSONDocument(
        "urlWebSite" -> news.urlWebSite,
        "urlNews" -> news.urlNews,
        "title" -> news.title,
        "summary" -> news.summary,
        "newsDate" -> BSONDateTime(date.getMillis),
        "corpus" -> news.corpus,
        "tags" -> news.tags,
        "metadescription" -> news.metaDescription,
        "metakeyword" -> news.metaKeyword,
        "canonicalUrl" -> news.canonicalUrl,
        "topImage" -> news.topImage,
        "processing" -> news.processing,
        "nlpAnalyzed" -> news.nlpAnalyzed)
    }
  }

  implicit object NewsBSONReader extends BSONDocumentReader[CrawledNews] {
    override def read(d: BSONDocument) =
      CrawledNews(
        id = d.getAs[BSONObjectID]("_id").map(_.toString()),
        urlWebSite = d.getAs[String]("urlWebSite").get,
        urlNews = d.getAs[String]("urlNews").get,
        title = d.getAs[String]("title").get,
        summary = d.getAs[String]("summary").get,
        newsDate = d.getAs[BSONDateTime]("newsDate").map(t => new DateTime(t.value)),
        corpus = d.getAs[String]("corpus").get,
        tags = d.getAs[Set[String]]("tags").get,
        metaDescription = d.getAs[String]("metaDescription").get,
        metaKeyword = d.getAs[String]("metaKeyword").get,
        canonicalUrl = d.getAs[String]("canonicalUrl").get,
        topImage = d.getAs[String]("topImage"),
        processing = d.getAs[Boolean]("processing").get,
        nlpAnalyzed = d.getAs[Boolean]("nlpAnalyzed").get)
  }

  implicit object WebControllerDataBSONReader extends BSONDocumentReader[CrawledWebSites] {
    override def read(doc: BSONDocument): CrawledWebSites = {
      CrawledWebSites(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("controllerName"),
        doc.getAs[List[String]]("stopUrls"),
        doc.getAs[Boolean]("enabled"))
    }
  }

  implicit object WebControllerDataBSONWriter extends BSONDocumentWriter[CrawledWebSites] {
    override def write(webController: CrawledWebSites): BSONDocument = BSONDocument(
      "controllerName" -> webController.controllerName,
      "stopUrls" -> webController.stopUrls,
      "enabled" -> webController.enabled)
  }

}
