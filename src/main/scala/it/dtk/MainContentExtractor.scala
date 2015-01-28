package it.dtk

import java.util.concurrent.Executor

import akka.actor._
import de.l3s.boilerpipe.extractors.ArticleExtractor
import it.dtk.db.DataModel._
import it.dtk.logic.GoseArticleExtractor
import akka.pattern._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.{ Failure, Success, Try }

object MainContentExtractor {

  case class Result(news: CrawledNews)

  case class FailContent(url: String, ex: Throwable)

  def props(baseUrl: String, record: DataRecord, httpRouter: ActorRef, when: FiniteDuration) =
    Props(classOf[MainContentExtractor],baseUrl, record, httpRouter,when)
}

/**
 * @author fabiana
 *
 */
class MainContentExtractor(baseUrl: String, record: DataRecord, routerHttpGetter: ActorRef, when: FiniteDuration)
    extends Actor with ActorLogging with GoseArticleExtractor {
  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  import it.dtk.MainContentExtractor._
  import HttpGetter._
  //schedule a message to fetch the html
  context.system.scheduler.scheduleOnce(when, routerHttpGetter, Get(record.newsUrl))

  def receive = {
    case Got(url, html, date) =>
      val article = extract(url, html)

      article.map { a =>

        val news = CrawledNews(
          urlWebSite = baseUrl,
          urlNews = url,
          title = record.title,
          summary = record.summary,
          newsDate = Option(record.newsDate),
          corpus = a.cleanedArticleText,
          tags = a.tags.toSet,
          metaDescription = a.metaDescription,
          metaKeyword = a.metaKeywords,
          canonicalUrl = a.canonicalLink,
          topImage = Option(a.topImage.imageSrc))

        Result(news)
      } recover {
        //wrong base usage but useful for wrapping the error
        case ex: Throwable =>
          context.parent ! FailContent(url,ex)
      } pipeTo context.parent

      context.stop(self)

    case Error(url,ex) =>
      context.parent ! FailContent(url,ex)
      context.stop(self)
  }

}

