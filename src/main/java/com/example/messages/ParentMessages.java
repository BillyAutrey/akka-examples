package com.example.messages;

import akka.actor.ActorRef;

public class ParentMessages {
    public static class RegisterReceiver{
        private ActorRef receiver;

        public RegisterReceiver(ActorRef ref){
            receiver = ref;
        }

        public ActorRef getReceiver(){
            return receiver;
        }
    }
}
