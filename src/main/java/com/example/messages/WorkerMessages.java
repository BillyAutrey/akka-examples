package com.example.messages;

import java.io.Serializable;

public class WorkerMessages {
    public static class Input implements Serializable{
        private String value;

        public Input(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }
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
