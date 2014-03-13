package it.dtk.util

import akka.testkit.TestKit
import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import org.scalatest.{BeforeAndAfterAll, WordSpecLike, Matchers}

class MySpec(actorSystemName: String) extends TestKit(ActorSystem(actorSystemName)) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }
}
