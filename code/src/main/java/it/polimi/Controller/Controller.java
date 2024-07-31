package it.polimi.Controller;

import it.polimi.Message.HelloMessage;

public class Controller {

    public static void handle(HelloMessage helloMessage) {
        System.out.println(helloMessage.getContent());
    }

}
