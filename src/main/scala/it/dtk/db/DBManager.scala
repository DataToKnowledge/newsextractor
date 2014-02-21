package it.dtk.db

import akka.actor.Actor
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.Mongo
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._

object DBManager {
  case class Save(record: News)
  case object Done
  case object Fail
  case object Killed
}

/**
 * persist data in the db MongoDB indexed based on url
 * we should use Casbah http://mongodb.github.io/casbah/
 *
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class DBManager(host: String, port: Int, database: String) extends Actor {

  import DBManager._
  
  val mongoClient = MongoClient(host,port)
  //val credentials = MongoCredential.createPlainCredential(userName, source, password)
  val db = mongoClient(database)

  def receive = {
    case Save(datarecord) =>
      //db.
      sender ! Done

    case "kill" =>
      context.stop(self)
      sender ! Killed

    case _ =>
      sender ! Fail
  }

}

class DBWorker(conn: MongoConnection) extends Actor{
  
  
  def receive = ???
}





