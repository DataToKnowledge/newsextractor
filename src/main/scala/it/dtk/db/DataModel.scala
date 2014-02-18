package it.dtk.db

import DataModel._
import java.util.Date

object DataModel {
  type Url = String
  type TagName = String
}

/**
 * @author fabiofumarola
 * @param id it is the progressive counter of the news extracted by the actor. the counter
 * resetted to 0 every day
 */
case class DataRecord(id: Long, urlWebSite: Url, urlNews: Url, title: String, newsDate: Date, 
    extractionDate: Date, summary: String, tags: Map[TagName,Url], text: String)