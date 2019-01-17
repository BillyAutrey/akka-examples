package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.example.actors.Parent;
import com.example.actors.Worker;
import com.example.messages.WorkerMessages;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.stream.IntStream;

public class Application {

    static public void main(String[] args) {

        if (args.length == 0) {
            startup(new String[]{"2551", "2552", "2553", "2554"});
        } else {
            startup(args);
        }
    }

    public static void startup(String[] ports){
        for (String port : ports) {
            // Override the configuration of the port
            Config config = ConfigFactory.parseString(
                    "akka.remote.artery.canonical.port=" + port)
                    .withFallback(
                            ConfigFactory.parseString("akka.cluster.roles = [parent,worker]"))
                    .withFallback(ConfigFactory.load());

            ActorSystem system = ActorSystem.create("ClusterSystem", config);

            //create parent as a singleton on all nodes
            ClusterSingletonManagerSettings settings = ClusterSingletonManagerSettings.create(system)
                    .withRole("parent");
            system.actorOf(ClusterSingletonManager.props(
                    Props.create(Parent.class), PoisonPill.getInstance(), settings),
                    "parent");

            //create the proxy to get to the parent
            ClusterSingletonProxySettings proxySettings =
                    ClusterSingletonProxySettings.create(system).withRole("parent");
            system.actorOf(ClusterSingletonProxy.props("/user/parent",
                    proxySettings), "parentProxy");

            //#create-workers
            system.actorOf(Props.create(Worker.class), "worker");
        }
    }

}
