package com.example;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.example.actors.Parent;
import com.example.actors.Worker;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

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
                    .withFallback(ConfigFactory.load());

            ActorSystem system = ActorSystem.create("ClusterSystem", config);

            //create based on the role
            if(port.equals("2551"))
                //#create-parent
                system.actorOf(Props.create(Parent.class), "parent");
            else
                //#create-workers
                system.actorOf(Props.create(Worker.class), "worker");
        }
    }

}
