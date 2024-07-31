package it.polimi.States;

import it.polimi.Message.HelloMessage;
import it.polimi.Message.Message;

public class HomeState implements RoomState{
    private static HomeState instance;
    public static HomeState getInstance() {
        if (instance == null) {
            instance = new HomeState();
        }
        return instance;
    }
    @Override
    public void handle(HelloMessage helloMessage) {
        System.out.println(helloMessage.getContent()+"in home state");
    }
}
