package com.example

import akka.remote.testkit.MultiNodeConfig
import com.typesafe.config.ConfigFactory

object AkkaClusterTestConfig extends MultiNodeConfig {
  // register the named roles (nodes) of the test
  val parent1 = role("parent1")
  val parent2 = role("parent2")
  val worker1 = role("worker1")
  val worker2 = role("worker2")
  val worker3 = role("worker3")

  // Set up different configs for different nodes
  // note that no fixed host names and ports are used

  private val workerConfig = ConfigFactory.parseString("""
    akka.loglevel = INFO
    akka.actor.provider = cluster
    akka.remote.log-remote-lifecycle-events = off
    akka.remote.enabled-transports = [akka.remote.netty.tcp]
    akka.cluster.roles = [worker]
    akka.actor.deployment {
      /parent/singleton/router {
          router = round-robin-group
          routees.paths = ["/user/worker"]
          cluster {
            enabled = on
            allow-local-routees = on
            use-roles = ["worker"]
          }
        }
    }
    """)

  private val parentConfig = ConfigFactory.parseString("""
    akka.loglevel = INFO
    akka.actor.provider = cluster
    akka.remote.log-remote-lifecycle-events = off
    akka.remote.enabled-transports = [akka.remote.netty.tcp]
    akka.cluster.roles = [worker,parent]
    akka.actor.deployment {
      /parent/singleton/router {
          router = round-robin-group
          routees.paths = ["/user/worker"]
          cluster {
            enabled = on
            allow-local-routees = on
            use-roles = ["worker"]
          }
        }
    }
    """)

  def nodeList = Seq(parent1, parent2, worker1, worker2, worker3)

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

  // assign configs to roles
  nodeConfig(parent1, parent2)(parentConfig)
  nodeConfig(worker1,worker2,worker3)(workerConfig)

}
