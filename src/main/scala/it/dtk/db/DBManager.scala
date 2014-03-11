package it.dtk.db

import akka.actor.Actor
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }
import scala.collection.mutable.ListBuffer
import scala.util.Failure
import akka.actor.ActorLogging

object DBManager {

  case object Done

  case class InsertNews(news: News)

  case class FailHandlingNews(news: News, ex: Throwable)

  case object ListWebControllers

  case class WebControllers(controllers: List[WebControllerData])

  case class FailQueryWebControllers(ex: Throwable)

  case class UpdateWebController(controller: WebControllerData)

}

class DBManager(host: String, database: String) extends Actor with ActorLogging {

  import DBManager._

  val driver = new MongoDriver
  //connect to the host
  val connection = driver.connection(List(host))

  //get a connection to the db
  val db = connection(database)
  //retrieve the collection
  val geoNews = db("geoNews")

  val webControllers = db("webControllers")

  def receive = {
    case InsertNews(datarecord) =>
      val future = geoNews.insert(datarecord)
      val dr = datarecord
      val send = sender
      future.onComplete {
        case Failure(e) =>
          send ! FailHandlingNews(dr, e)

        case Success(lastError) =>
          log.info("successfully inserted document: " + lastError)
          send ! Done
      }

    case ListWebControllers =>

      val query = BSONDocument("enabled" -> true)

      val controllers = webControllers.find(query).cursor[WebControllerData].collect[List]()

      val send = sender

      controllers.map(
        result => send ! WebControllers(result)) recover {
          case ex => send ! FailQueryWebControllers(ex)
        }

    case UpdateWebController(c) =>

      import WebControllerData._

      val selector = BSONDocument("_id" -> c.id)
      val update = BSONDocument("$set" -> BSONDocument("stopUrls" -> c.stopUrls))

      val send = sender

      val futureUpdate = webControllers.update(selector, update)

      futureUpdate.onComplete {
        case Failure(e) => 
          send ! FailQueryWebControllers(e)
        case Success(lasterror) => {
          log.info("successfully update document")
        }
      }
  }

  override def postStop(): Unit = {
    driver.close()
  }

}




