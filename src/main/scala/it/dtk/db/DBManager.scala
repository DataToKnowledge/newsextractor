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

/**
 * persist data in the db MongoDB indexed based on url
 * we should use Casbah http://mongodb.github.io/casbah/
 *
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
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
          println(sender.path)
        }
      }
  }

  override def postStop(): Unit = {
    driver.close
  }

}






