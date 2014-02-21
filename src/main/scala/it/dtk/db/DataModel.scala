package it.dtk.db

import java.util.Date
import org.joda.time.DateTime
import com.novus.salat.annotations.raw.Key
import org.bson.types.ObjectId

object DataModel {
  type Url = String
  type TagName = String

}

/**
 * @author fabiofumarola
 * @param id it is the progressive counter of the news extracted by the actor. the counter is reset to 0 every day
 */
case class News(@Key("_id") id: ObjectId = new ObjectId, prog: Long, urlWebSite: String, urlNews: String, title: String, summary: String, newsDate: Date,
                text: Option[String] = None, tags: Option[Set[String]] = None, metaDescription: Option[String] = None,
                metaKeyword: Option[String] = None, canonicalUrl: Option[String] = None,
                topImage: Option[String] = None) {

  val extractionDate: Date = (new DateTime).toDate
  

}