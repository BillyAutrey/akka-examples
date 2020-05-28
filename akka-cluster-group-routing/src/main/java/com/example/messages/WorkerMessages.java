package com.example.messages;

import akka.actor.ActorRef;

import java.io.Serializable;

public class WorkerMessages {
    public static class Input implements Serializable{
        private String value;
        private ActorRef replyTo;

        public Input(String value, ActorRef replyTo){
            this.value = value;
            this.replyTo = replyTo;
        }

        public String getValue(){
            return value;
        }
        public ActorRef getReplyTo() {return replyTo;}
    }

    public static class ProcessCount implements Serializable {
        private Integer value;

        public ProcessCount(Integer value){
            this.value = value;
        }

        public Integer getValue(){
            return value;
        }
    }
}
