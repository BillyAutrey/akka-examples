package com.example.messages;

import akka.actor.ActorRef;

import java.io.Serializable;

public class ParentMessages {
    public static class RegisterReceiver implements Serializable {
        private ActorRef receiver;

        public RegisterReceiver(ActorRef ref){
            receiver = ref;
        }

        public ActorRef getReceiver(){
            return receiver;
        }
    }
}
