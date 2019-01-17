package com.example.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import com.example.messages.WorkerMessages.Input;
import com.example.messages.WorkerMessages.ProcessCount;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class Client extends AbstractLoggingActor {

    final Cancellable tick;
    final ActorRef parentPath;

    Cluster cluster = Cluster.get(getContext().system());

    public Client(ActorRef parentPath) {
        this.parentPath = parentPath;
        FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
        tick = getContext().system().scheduler()
                .schedule(interval,interval,parentPath,"tick",getContext().dispatcher(),getSelf());
    }

    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
