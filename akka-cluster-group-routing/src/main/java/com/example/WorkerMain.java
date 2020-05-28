package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import com.example.actors.Client;
import com.example.actors.Worker;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class WorkerMain {

    static public void main(String[] args) {
        Config config = ConfigFactory.parseString("akka.cluster.roles = [worker]")
                .withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("ClusterSystem", config);

        //create a worker
        system.actorOf(Props.create(Worker.class),"worker");
    }

}
