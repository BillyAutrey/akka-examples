package com.example.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import com.example.messages.ParentMessages.RegisterReceiver
import com.example.messages.WorkerMessages.Input
import org.scalacheck.Prop.forAll
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._

class ParentTest extends TestKit(ActorSystem("ParentTest"))
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val log: Logger = LoggerFactory.getLogger(getClass)

  override def beforeAll: Unit = {
    log.info("Initializing actors")

  }

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Parent" must {
    "route messages to several workers" in {
      val parent = system.actorOf(Props[Parent],"parent")
      val workers = system.actorOf(Props[Workers],"workers")
      val probe = TestProbe()
      parent ! new RegisterReceiver(probe.ref)

      val w1 = "akka://ParentTest/user/workers/w1"
      val w2 = "akka://ParentTest/user/workers/w2"
      val w3 = "akka://ParentTest/user/workers/w3"
      val resultSet = List(w1,w2,w3)

      val input = Gen.listOfN(100,arbitrary[String]).sample.getOrElse(List.empty)

      input.foreach(parent ! _)

      val messages = probe.receiveWhile[String](messages = 8){
        case value: String => value
      }

      assert(resultSet.forall(messages.contains(_)))

    }
  }

}
