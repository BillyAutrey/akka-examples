package com.example.actors;

import akka.actor.AbstractLoggingActor;
import com.example.messages.WorkerMessages.*;

public class Worker extends AbstractLoggingActor {
    Integer count = 0;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Input.class, this::handleInput)
        .build();
    }

    public void handleInput(Input input){
        log().debug("Got input for {}",input.getValue());
        count++;
        if(count == 10){
            log().info("Processed 10 messages");
            count = 0;
            sender().tell(new ProcessCount(10),context().self());
        }
    }
}
