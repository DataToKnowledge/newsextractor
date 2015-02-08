package it.dtk

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern._
import it.dtk.DataModel._
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.core.nodeset.Authenticate
import scala.util._

object MongoDbActor {

  case class Save(news: CrawledNews)

  case object ListWebControllers

  case class WebControllers(controllers: List[CrawledWebSites])

  case class UpdateWebController(controller: CrawledWebSites)

  case class DBFailure(msg: AnyRef, ex: Throwable)

  /**
   * Create Props for the database actor
   */
  def props() = Props(classOf[MongoDbActor])

}

class MongoDbActor extends Actor with ActorLogging with MongoDbMappings {

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
            crawledNews.insert(record) pipeTo send
          }
          else{
            send ! DBFailure("record already inserted", new Error("record already inserted"))
          }

        case Failure(ex) =>
          send ! DBFailure("error with the db", ex)
      }

    case ListWebControllers =>
      val send = sender
      val query = BSONDocument("enabled" -> true)
      val result = crawledWebSites.
        find(query).cursor[CrawledWebSites].collect[List]()

      result.map(WebControllers(_)).recover {
        case ex: Throwable =>
          send ! DBFailure(ListWebControllers, ex)
      } pipeTo send

    case u @ UpdateWebController(c) =>
      val selector = BSONDocument("controllerName" -> c.controllerName)
      val update = BSONDocument("$set" -> BSONDocument("stopUrls" -> c.stopUrls))

      val send = sender
      crawledWebSites.update(selector, update).
        recover {
          case ex: Throwable =>
            send ! DBFailure(u, ex)
        } pipeTo send
  }

  override def postStop(): Unit = {
    driver.close()
  }

}
