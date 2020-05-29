name := "akka-examples"

version := "0.1"

scalaVersion := "2.13.2"
lazy val akkaVersion = "2.6.5"

lazy val root = (project in file("."))
  .aggregate(`akka-cluster-group-routing`,`akka-streams-examples`)

lazy val `akka-cluster-group-routing` = (project in file("akka-cluster-group-routing"))
  .enablePlugins(MultiJvmPlugin)
  .configs(MultiJvm)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,

      //cluster
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,

      //test
      "org.scalatest" %% "scalatest" % "3.0.5" % Test, //scala's jUnit equivalent
      "org.scalacheck" %% "scalacheck" % "1.14.0" % Test, //property testing

      //logging
      "ch.qos.logback" % "logback-classic" % "1.2.3"

    )
  )

lazy val `akka-streams-examples` = (project in file("akka-streams-examples"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.5" % Test,

      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )