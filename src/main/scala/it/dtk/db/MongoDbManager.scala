package it.dtk.db

import akka.actor.{ ActorLogging, Actor }
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import akka.actor.Props
import akka.pattern._
import DataModel._

object MongoDbManager {

  case class Save(news: CrawledNews)

  case object ListWebControllers

  case class WebControllers(controllers: List[CrawledWebSites])

  case class UpdateWebController(controller: CrawledWebSites)

  case class DBFailure(msg: AnyRef, ex: Throwable)

  /**
   * Create Props for the database actor
   */
  def props() = Props(classOf[MongoDbManager])

}

class MongoDbManager() extends Actor with ActorLogging with MongoDbMappings {

  val config = context.system.settings.config
  val mongoConf = config.getConfig("newsExtractor.mongo")
  val host = mongoConf.getString("host")
  val port = mongoConf.getInt("port")

  val dbName = mongoConf.getString("dbName")
  val crawledNewsName = mongoConf.getString("crawledNews")
  val controllerColl = mongoConf.getString("controllers")

  import MongoDbManager._
  implicit val exec = context.dispatcher

  val driver = new MongoDriver
  val connection = driver.connection(List(host))
  val db = connection(dbName)

  val crawledNews: BSONCollection = db(crawledNewsName)
  val webControllers: BSONCollection = db(controllerColl)

  def receive: Receive = {

    case Save(record) =>
      val selector = BSONDocument("urlNews" -> record.urlNews)
      val send = sender
      crawledNews.update(selector, record, upsert = true) pipeTo send

    case ListWebControllers =>
      val send = sender
      val query = BSONDocument("enabled" -> true)
      val result = webControllers.
        find(query).cursor[CrawledWebSites].collect[List]()

      result.map(WebControllers(_)).recover {
        case ex: Throwable =>
          send ! DBFailure(ListWebControllers, ex)
      } pipeTo send

    case u @ UpdateWebController(c) =>
      val selector = BSONDocument("controllerName" -> c.controllerName)
      val update = BSONDocument("$set" -> BSONDocument("stopUrls" -> c.stopUrls))

      val send = sender
      webControllers.update(selector, update).
        recover {
          case ex: Throwable =>
            send ! DBFailure(u, ex)
        } pipeTo send
  }

  override def postStop(): Unit = {
    driver.close()
  }

}
