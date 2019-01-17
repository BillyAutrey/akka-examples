package com.example.actors;

import akka.actor.AbstractLoggingActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import com.example.messages.WorkerMessages.*;

public class Worker extends AbstractLoggingActor {
    Integer count = 0;

    Cluster cluster = Cluster.get(getContext().system());

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
        return receiveBuilder().match(Input.class, this::handleInput)
        .build();
    }

    public void handleInput(Input input){
        log().info("Got input for {}",input.getValue());
        count++;
        if(count == 10){
            log().info("Processed 10 messages");
            count = 0;
            input.getReplyTo().tell(new ProcessCount(10),context().self());
        }
    }
}
