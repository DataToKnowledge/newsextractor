package it.dtk.db

import akka.actor.{ ActorLogging, Actor }
import reactivemongo.api.MongoDriver
import reactivemongo.api.collections.default.BSONCollection
import scala.util.{ Success, Failure }
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.Props

object DBManager {

  case object DoneInsertNews

  case class InsertNews(news: News)

  case class FailHandlingNews(news: News, ex: Throwable)

  case object DoneUpdateWebController

  case object ListWebControllers

  case class WebControllers(controllers: List[WebControllerData])

  case class FailQueryWebControllers(ex: Throwable)

  case class UpdateWebController(controller: WebControllerData)

  /**
   * Create Props for the database actor
   * @param host
   * @param database
   * @return
   */
  def props(host: String, database: String) = Props(classOf[DBManager], host, database)

}

class DBManager(host: String, database: String) extends Actor with ActorLogging {

  import DBManager._

  val driver = new MongoDriver
  //connect to the host
  val connection = driver.connection(List(host))

  //get a connection to the db
  val db = connection(database)
  //retrieve the collection
  val geoNews = db[BSONCollection]("geoNews")

  val webControllers = db[BSONCollection]("webControllers")
  
  def receive = {
    case InsertNews(datarecord) =>
      val selector = BSONDocument("urlNews" -> datarecord.urlNews)
      val future = geoNews.update(selector, datarecord, upsert = true)
      val dr = datarecord
      val send = sender
      future.onComplete {
        case Success(lastError) =>
          //log.info("Successfully inserted news with URL {}", dr.urlNews.getOrElse("<UNDEFINED>"))
          send ! DoneInsertNews

        case Failure(e) =>
          //log.error("Failed to insert news with URL {}", dr.urlNews.getOrElse("<UNDEFINED>"))
          send ! FailHandlingNews(dr, e)
      }

    case ListWebControllers =>
      val query = BSONDocument("enabled" -> true)
      val controllers = webControllers.find(query).cursor[WebControllerData].collect[List]()
      val send = sender
      controllers.map(
        result => send ! WebControllers(result)) recover {
          case ex => 
            send ! FailQueryWebControllers(ex)
            throw ex
        }

    case UpdateWebController(c) =>
      val selector = BSONDocument("controllerName" -> c.controllerName)
      val update = BSONDocument("$set" -> BSONDocument("stopUrls" -> c.stopUrls))
      val send = sender
      val futureUpdate = webControllers.update(selector, update)
      futureUpdate.onComplete {
        case Failure(e) =>
          //log.error("Failed to update WebController {}", c.controllerName.getOrElse("<UNDEFINED>"))
          send ! FailQueryWebControllers(e)

        case Success(lastError) =>
          //log.info("Successfully updated WebController {}", c.controllerName.getOrElse("<UNDEFINED>"))
          send ! DoneUpdateWebController
      }
  }

  override def postStop(): Unit = {
    driver.close()
  }

}
