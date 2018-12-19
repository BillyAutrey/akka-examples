package com.example.messages;

public class WorkerMessages {
    public static class Input{
        private String value;

        public Input(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }
    }

    public static class ProcessCount{
        private Integer value;

        public ProcessCount(Integer value){
            this.value = value;
        }

        public Integer getValue(){
            return value;
        }
    }
}
