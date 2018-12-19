package com.example;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.example.actors.Parent;
import com.example.actors.Workers;

public class Application {

    static public void main(String[] args){
        final ActorSystem system = ActorSystem.create("parent");

        //#create-workers
        system.actorOf(Props.create(Workers.class), "workers");

        //#create-parent
        system.actorOf(Props.create(Parent.class), "parent");
    }

}
