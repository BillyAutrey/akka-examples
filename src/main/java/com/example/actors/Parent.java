package com.example.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.*;
import akka.routing.FromConfig;
import com.example.messages.WorkerMessages.*;

public class Parent extends AbstractActor {

    //#round-robin-group
    private ActorRef router = getContext().actorOf(FromConfig.getInstance().props(Props.empty()), "router");

    Cluster cluster = Cluster.get(getContext().system());

    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(self(), MemberUp.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, input -> workString(input,sender()))
            .build();
    }

    public void workString(String input, ActorRef sender){
        router.tell(new Input(input,sender), context().self());
    }


}
