package com.example.streams

import akka.actor.typed.ActorSystem
import akka.actor.typed.ActorRef
import akka.stream.CompletionStrategy
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.typed.scaladsl.ActorSource
import com.example.actors.RequestHandler
import com.example.actors.RequestHandler._

import scala.util.Success

class ActorRefStreamer(ackTo: ActorRef[Ack.type])(implicit system: ActorSystem[Request]) {
  val source = ActorSource.actorRefWithBackpressure[StreamRequest,Ack.type](
    ackTo,
    Ack,
    { case _: StreamComplete.type => CompletionStrategy.immediately},
    PartialFunction.empty)

  def stream() = {
    source.to(Sink.foreach{
      case StreamMsg(msg) =>
        system.log.info("Stream processed - {}",msg)
    }).run()
  }
}
