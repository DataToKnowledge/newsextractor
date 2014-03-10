package it.dtk.db

import akka.actor.Actor
import reactivemongo.api.MongoDriver
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern._
import scala.util.Success
import scala.util.Failure

object DBManager {
  case class Insert(record: News)
  case object Done
  case class Fail(record: News)
}

class DBManager(host: String, database: String) extends Actor {

  import DBManager._
  import News._

  val driver = new MongoDriver
  //connect to the host
  val connection = driver.connection(List(host))

  //get a connection to the db
  val db = connection(database)
  //retrieve the collection
  val geoNews = db("geoNews")

  def receive = {
    case Insert(datarecord) =>
      val send = sender
      val future = geoNews.insert(datarecord)
      future.onComplete {
        case Failure(e) => {
          send ! Fail(datarecord)
        }
        case Success(lastError) => {
          println("successfully inserted document: " + lastError)
          send ! Done
        }
      }
  }

  override def postStop(): Unit = {
    driver.close
  }

}




