package com.example

import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.typed.scaladsl.ActorSink
import com.example.actors.RequestHandler
import com.example.actors.RequestHandler._
import com.example.streams.ActorRefStreamer

import scala.concurrent.duration._

object StreamsExamples extends App {
  implicit val system = ActorSystem[Request](RequestHandler(),"streams-example")

  val streamer = new ActorRefStreamer(system).stream()
  system ! SetStream(streamer)

  Source.tick[Request](1.second, 2.seconds, MsgRequest("ping")).map( request => system ! request).run()
}
