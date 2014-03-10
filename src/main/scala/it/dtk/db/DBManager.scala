package it.dtk.db

import akka.actor.Actor
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.collection.mutable.ListBuffer

object DBManager {

  case class Insert(record: News)

  case object Done

  case class Fail(record: News)

  case object Load

  case class WebControllers(controllers: List[WebControllerData])

}

class DBManager(host: String, database: String) extends Actor {

  import DBManager._

  val driver = new MongoDriver
  //connect to the host
  val connection = driver.connection(List(host))

  //get a connection to the db
  val db = connection(database)
  //retrieve the collection
  val geoNews = db("geoNews")

  def receive = {
    case Insert(datarecord) =>
      val future = geoNews.insert(datarecord)
      future.onComplete {
        case Failure(e) =>
          sender ! Fail(datarecord)

        case Success(lastError) =>
          println("successfully inserted document: " + lastError)
          sender ! Done
      }

    case Load =>
      val webControllers = db("webControllers")
      val query = BSONDocument("enabled" -> 1)
      val filter = BSONDocument("props" -> 1)

      val controllers = webControllers.find(query, filter).cursor[WebControllerData].collect[List]() 
      
      controllers.map(results => sender ! WebControllers(results))
      
  }

  override def postStop(): Unit = {
    driver.close()
  }

}




