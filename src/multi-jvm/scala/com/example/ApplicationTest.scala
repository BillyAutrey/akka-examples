package com.example

import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import com.example.actors.{Parent, Worker}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers, WordSpecLike}

import scala.concurrent.duration._

// need one concrete test class per node
class StatsSampleSpecMultiJvmNode1 extends ApplicationTest
class StatsSampleSpecMultiJvmNode2 extends ApplicationTest
class StatsSampleSpecMultiJvmNode3 extends ApplicationTest

abstract class ApplicationTest extends MultiNodeSpec(AkkaClusterTestConfig)
  with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  import AkkaClusterTestConfig._
  override def initialParticipants = roles.size

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()
  "Application" must {
    "illustrate how to startup cluster" in within(15.seconds) {
      Cluster(system).subscribe(testActor, classOf[MemberUp])
      expectMsgClass(classOf[CurrentClusterState])

      val firstAddress = node(parent).address
      val secondAddress = node(worker1).address
      val thirdAddress = node(worker2).address

      Cluster(system) join firstAddress

      system.actorOf(Props[Parent], "parent")
      system.actorOf(Props[Worker], "worker")

      receiveN(3).collect { case MemberUp(m) ⇒ m.address }.toSet should be(
        Set(firstAddress, secondAddress, thirdAddress))

      Cluster(system).unsubscribe(testActor)

      testConductor.enter("all-up")
    }
  }
}
