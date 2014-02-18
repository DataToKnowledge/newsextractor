package it.dtk

import akka.actor.Actor

object MainContentExtractor {
  case class Result(title: String, canonicalUrl: String, extractedTags: Seq[String], metaKeywords: Seq[String], metaDescription: String,
      articleText: String, imageUrl: String)
  
}


/**
 * @author fabiofumarola
 *
 */
class MainContentExtractor(html: String) extends Actor {

  def receive = ???
}