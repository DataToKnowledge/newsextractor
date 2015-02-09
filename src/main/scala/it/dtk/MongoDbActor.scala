package it.dtk

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern._
import it.dtk.DataModel._
import org.joda.time.DateTime
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._
import reactivemongo.core.nodeset.Authenticate
import scala.util._

object MongoDbActor {

  case class Save(news: CrawledNews)

  case object ListWebControllers

  case class WebControllers(controllers: List[CrawledWebSites])

  case class LastUrlNewsRequest(controllerName: String, urlWebSite: String)
  case class LastUrlNewsResponse(controllerName: String, url: Option[String])
  case class LastUrlNewsFailure(controllerName: String, ex: Throwable)

  case class DBFailure(msg: AnyRef, ex: Throwable)

  /**
   * Create Props for the database actor
   */
  def props() = Props(classOf[MongoDbActor])

}

class MongoDbActor extends Actor with ActorLogging {

  val config = context.system.settings.config
  val mongoConf = config.getConfig("newsExtractor.mongo")
  val host = mongoConf.getString("host")
  val port = mongoConf.getInt("port")

  val dbName = mongoConf.getString("dbName")
  val username = mongoConf.getString("username")
  val password = mongoConf.getString("password")
  val crawledNewsName = mongoConf.getString("crawledNews")
  val controllerColl = mongoConf.getString("controllers")

  import it.dtk.MongoDbActor._
  implicit val exec = context.dispatcher

  val driver = new MongoDriver
  val credentials = List(Authenticate(dbName, username, password))

  val connection = driver.connection(List(host), authentications = credentials)
  val db = connection(dbName)

  val crawledNews: BSONCollection = db(crawledNewsName)
  val crawledWebSites: BSONCollection = db(controllerColl)

  def receive: Receive = {

    case Save(record) =>
      val send = sender

      val selector = BSONDocument("urlNews" -> record.urlNews)
      //find by url
      val ifExist = crawledNews.find(selector).cursor[BSONDocument].
        collect[List]()

      ifExist.onComplete {
        case Success(list) =>
          if (list.size == 0) {

            val doc = writeCrawledNews(record)
            crawledNews.insert(doc) pipeTo send
          }
          else {
            send ! DBFailure("record already inserted", new Error("record already inserted"))
          }

        case Failure(ex) =>
          send ! DBFailure("error with the db", ex)
      }

    case ListWebControllers =>
      val send = sender
      val query = BSONDocument("enabled" -> true)
      val result = crawledWebSites.
        find(query).cursor[BSONDocument].collect[List]().map(_.map(readCrawledWebSites))

      result.map(WebControllers(_)).recover {
        case ex: Throwable =>
          send ! DBFailure(ListWebControllers, ex)
      } pipeTo send

    case LastUrlNewsRequest(controllerName, urlWebSite) =>
      //db.crawledNews.find({urlWebSite: "http://bari.repubblica.it/"}).sort({_id: -1}).limit(1)
      val send = sender
      val lastUrls = crawledNews.
        find(BSONDocument("urlWebSite" -> urlWebSite)).
        sort(BSONDocument("_id" -> -1)).
        cursor[BSONDocument].collect[List](1)

      lastUrls.onComplete {
        case Success(list) =>
          val url = list.map(doc => doc.getAs[String]("urlNews"))
          send ! LastUrlNewsResponse(controllerName, url.headOption.flatMap(x => identity(x)))

        case Failure(ex) =>
          send ! LastUrlNewsFailure(controllerName, ex)
      }

  }

  override def postStop(): Unit = {
    driver.close()
  }

  def writeCrawledNews(news: CrawledNews): BSONDocument = {

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

  def readCrawledNews(d: BSONDocument) =
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

  def readCrawledWebSites(doc: BSONDocument): CrawledWebSites =
    CrawledWebSites(
      doc.getAs[BSONObjectID]("_id"),
      doc.getAs[String]("webSiteName"),
      doc.getAs[String]("urlWebSite"),
      doc.getAs[String]("controllerName"),
      doc.getAs[Boolean]("enabled"))

  def writeCrawledWebSites(webController: CrawledWebSites) =
    BSONDocument(
      "webSiteName" -> webController.webSiteName,
      "urlWebSite" -> webController.urlWebSite,
      "controllerName" -> webController.controllerName,
      "enabled" -> webController.enabled)

}
