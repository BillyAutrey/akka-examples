package com.example

import akka.remote.testkit.MultiNodeConfig
import com.typesafe.config.ConfigFactory

object AkkaClusterTestConfig extends MultiNodeConfig {
  // register the named roles (nodes) of the test
  val parent = role("parent")
  val worker1 = role("worker")
  val worker2 = role("worker")

  def nodeList = Seq(parent, worker1, worker2)

  // Extract individual sigar library for every node.
  nodeList foreach { role ⇒
    nodeConfig(role) {
      ConfigFactory.parseString(s"""
      # Enable metrics extension in akka-cluster-metrics.
      akka.extensions=["akka.cluster.metrics.ClusterMetricsExtension"]
      # Sigar native library extract location during tests.
      akka.cluster.metrics.native-library-extract-folder=target/native/${role.name}
      """)
    }
  }

  // this configuration will be used for all nodes
  // note that no fixed host names and ports are used
  commonConfig(ConfigFactory.parseString("""
    akka.actor.provider = cluster
    akka.remote.log-remote-lifecycle-events = off
    akka.cluster.roles = [worker]
    akka.actor.deployment {
      /statsService/workerRouter {
          router = consistent-hashing-group
          routees.paths = ["/user/worker"]
          cluster {
            enabled = on
            allow-local-routees = on
            use-roles = ["worker"]
          }
        }
    }
    """))
}
