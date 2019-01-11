package com.example.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.*;
import akka.routing.FromConfig;
import com.example.messages.ParentMessages.RegisterReceiver;
import com.example.messages.WorkerMessages.*;

public class Parent extends AbstractActor {

    //#round-robin-group
    private ActorRef router = getContext().actorOf(FromConfig.getInstance().props(), "router");
    private ActorRef reportReceiver;

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
            .match(String.class, this::workString)
            .match(ProcessCount.class, this::handleCount)
            .match(RegisterReceiver.class, this::registerReceiver)
            .build();
    }

    public void workString(String input){
        router.tell(new Input(input), context().self());
    }

    public void handleCount(ProcessCount count){
        reportReceiver.tell(sender().path().toString(), context().self());
    }

    public void registerReceiver(RegisterReceiver receiver){
        this.reportReceiver = receiver.getReceiver();
    }

}
