package com.example.actors

import akka.actor.Stash
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer}

object RequestHandler {
  sealed trait Request
  case object Ack extends Request
  case object RequestComplete extends Request
  case class MsgRequest(msg: String) extends Request
  trait StreamRequest extends Request
  case class StreamMsg(msg: String) extends StreamRequest
  case object StreamComplete extends StreamRequest
  case class SetStream(actorRef: ActorRef[StreamRequest]) extends Request

  def apply(): Behavior[Request] =
    Behaviors.setup[Request]{context =>
      Behaviors.withStash[Request](20){ stash =>
        waitForStream(context, stash)
      }
    }

  def waitForStream(context: ActorContext[Request], stash: StashBuffer[Request]): Behaviors.Receive[Request] =
    Behaviors.receiveMessage[Request]{
      case SetStream(actorRef) => listen(context,stash,actorRef)
      case request =>
        stash.stash(request)
        Behaviors.same
    }

  def listen(
              context: ActorContext[Request],
              stash: StashBuffer[Request],
              stream: ActorRef[StreamRequest]
            ): Behaviors.Receive[Request] =
    Behaviors.receiveMessage[Request]{
      case Ack =>
        context.log.info("Received an Ack when listening")
        Behaviors.same
      case MsgRequest(msg) =>
        stream ! StreamMsg(msg)
        backpressure(context,stash,stream)
    }

  def backpressure(
             context: ActorContext[Request],
             stash:StashBuffer[Request],
             stream: ActorRef[StreamRequest]
           ): Behaviors.Receive[Request] =
    Behaviors.receiveMessage[Request]{
      case Ack =>
        context.log.info("Received Ack, listening")
        stash.unstash(listen(context,stash,stream),1, msg => msg)
      case msg =>
        context.log.info("Storing a message that we're not ready for")
        stash.stash(msg)
        Behaviors.same
    }
}
