package it.dtk.db

import DataModel._
import java.util.Date
import org.joda.time.DateTime

object DataModel {
  type Url = String
  type TagName = String
  
}

/**
 * @author fabiofumarola
 * @param id it is the progressive counter of the news extracted by the actor. the counter is reset to 0 every day
 */
case class DataRecord(id: Long, urlWebSite: String,urlNews: String, title: String, summary: String, 
    newsDate: Date, extractionDate: Date = (new DateTime).toDate, tags: Map[String,Url], 
    metaDescription: String, metaKeyword: Map[String,String], text: String)