//package it.wheretolive.newsextractor
//
//import akka.actor.Actor.Receive
//import akka.actor.{Props, Actor, ActorLogging}
//import akka.contrib.throttle.Throttler.Rate
//import akka.contrib.throttle.Throttler.SetTarget
//import akka.contrib.throttle.TimerBasedThrottler
//import akka.contrib.throttle.TimerBasedThrottler
//import it.wheretolive.newsextractor.logic.BariToday
//import scala.concurrent.duration._
//import MongoDbActor._
//
//
//object NewsPaperController {
//
//
//}
//
///**
// * Created by fabiofumarola on 08/02/15.
// */
//class NewsPaperController extends Actor with ActorLogging with BariToday {
//  import MessageProtocol._
//
//  object Start
//
//  val httpRouter = context.actorOf(HttpActor.routerProps(),"BariTodayHttpRouter")
//
//  val throttler = context.actorOf(Props(classOf[TimerBasedThrottler], new Rate(call,1.second)))
//  throttler ! SetTarget(Some(httpRouter))
//
//  val dataRecordActor = context.actorOf(dataRecordProps)
//
//  val dataArticleActorRouter = context.actorOf(ArticleExtractorActor.routerProps(),"ArticleRouter")
//
//  val dbActor = context.actorOf(MongoDbActor.props())
//
//  var latestNewsUrl: Option[String] = None
//  var currentIndex = 0
//
//  override def receive: Receive = {
//
//    case Process =>
//      dbActor ! FindLatestNewsUrl
//
//    case UrlNews(lastUrl) =>
//      latestNewsUrl = lastUrl
//
//    case Start =>
//      val url = generateUrl(currentIndex)
//      dataArticleActorRouter ! DataRecordRequest(url)
//
//    case DataRecordResponse(list) =>
//
//
//  }
//
//}
//
