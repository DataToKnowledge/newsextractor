package it.dtk.db

import akka.actor.Actor
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

object DBManager {
  case class Save(record: News)

  case class Done(record: News)

  case class Fail(record: News)

  case class SaveException(record: News, message: String) extends Throwable

  case object Killed
}

/**
 * persist data in the db MongoDB indexed based on url
 * Casbah Documentation: http://mongodb.github.io/casbah/
 *
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class DBManager(host: String, port: Int, database: String = "extractor", collection: String = "news") extends Actor {

  import DBManager._

  val mongoClient = MongoClient(host, port)
  // TODO: Login with credentials
  //val credentials = MongoCredential.createPlainCredential(userName, source, password)
  val db = mongoClient(database)
  val newsCollection = db(collection)

  def receive = {

    case Save(record: News) =>
      try {
        // FIXME: Is better to query for title or URL?
        val query = MongoDBObject("title" -> record.title)
        val newsObject = MongoDBObject(
          "extractionId" -> record.id,
          "website" -> record.urlWebSite,
          "url" -> record.urlNews,
          "title" -> record.title,
          "summary" -> record.summary,
          "date" -> record.newsDate.toString,
          "extractionDate" -> record.extractionDate.toString,
          "text" -> record.text.getOrElse("None"),
          "tags" -> record.tags.getOrElse("None"),
          "metaDescription" -> record.metaDescription.getOrElse("None"),
          "metaKeyword" -> record.metaKeyword.getOrElse("None"),
          "canonicalUrl" -> record.canonicalUrl.getOrElse("None"),
          "image" -> record.topImage.getOrElse("None")
        )
        // Note: with upsert enabled, document will be created if not exists
        val result = newsCollection.update(query, newsObject, upsert = true)

        if (result.getN > 0)
          sender ! Done(record)
        else
          sender ! Fail(record)
      } catch {
        case e: Exception =>
          sender ! SaveException(record, e.getMessage)
      }

    case "kill" =>
      // FIXME: Must we finalize db connection, if active?
      sender ! Killed
      context.stop(self)
  }

}



