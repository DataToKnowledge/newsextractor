package it.wheretolive.newsextractor

import DataModel._
import akka.actor.{Props, Actor, ActorLogging}
import akka.routing.RoundRobinPool
import it.wheretolive.newsextractor.MessageProtocol.ArticleRequest
import logic._

object ArticleExtractorActor {


  def props = Props(classOf[ArticleExtractorActor])

  def routerProps(nroInstance: Int = 2) =
    RoundRobinPool(nroInstance).props(props)
}
/**
 * Created by fabiofumarola on 08/02/15.
 */
class ArticleExtractorActor extends Actor with ActorLogging with GoseArticleExtractor {


  override def receive: Receive = {

    case ArticleRequest(url, html) =>

      //val data = extract(url,html)

  }
}
