package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import com.example.actors.Client;
import com.example.actors.Parent;
import com.example.actors.Worker;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClientMain {

    static public void main(String[] args) {
        ActorSystem system = ActorSystem.create("ClusterSystem", ConfigFactory.load());

        //access via proxy
        ClusterSingletonProxySettings proxySettings =
                ClusterSingletonProxySettings.create(system).withRole("parent");

        ActorRef proxy =
                system.actorOf(ClusterSingletonProxy.props("/user/parent", proxySettings),
                        "parentProxy");

        //create a client
        system.actorOf(Props.create(Client.class, proxy),"client");
    }

}
