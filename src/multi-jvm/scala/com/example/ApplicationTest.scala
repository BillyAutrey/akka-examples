package com.example

import akka.actor.{PoisonPill, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import com.example.actors.{Parent, Worker}
import com.example.messages.WorkerMessages.ProcessCount
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

// need one concrete test class per node
class ParentWorkerMultiJvmNode1 extends ApplicationTest
class ParentWorkerMultiJvmNode2 extends ApplicationTest
class ParentWorkerMultiJvmNode3 extends ApplicationTest
class ParentWorkerMultiJvmNode4 extends ApplicationTest

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

      val parentAddress = node(parent).address
      val worker1Address = node(worker1).address
      val worker2Address = node(worker2).address
      val worker3Address = node(worker3).address

      Cluster(system) join parentAddress

      //creating parent as a singleton on every node.
      system.actorOf(ClusterSingletonManager.props(
        Props[Parent],
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(system).withRole("parent")),
        name = "parent")

      //creating a proxy to access the parent
      system.actorOf(ClusterSingletonProxy.props(
        singletonManagerPath = "/user/parent",
        settings = ClusterSingletonProxySettings(system).withRole("parent")
      ), "parentProxy")

      system.actorOf(Props[Worker], "worker")

      receiveN(4).collect { case MemberUp(m) ⇒ m.address }.toSet should be(
        Set(parentAddress, worker1Address,worker2Address,worker3Address))

      Cluster(system).unsubscribe(testActor)

      testConductor.enter("all-up")
    }

    "send messages across the cluster" in within(15.seconds) {
      val parentRef = system.actorSelection(RootActorPath(node(parent).address)/"user"/"parentProxy")

      val input = (1 to 300).map(_.toString)

      //We only want one actor sending and receiving client messages.
      runOn(worker2) {
        input.foreach(parentRef ! _)
        val messages = receiveWhile[ProcessCount](messages = 8){
          case value: ProcessCount => value
        }

        assert(messages.size >= 8)
      }

      testConductor.enter("done")
    }

  }
}
