package it.polimi.States;

import it.polimi.Message.HelloMessage;
import it.polimi.Message.Message;

public class InRoomState implements RoomState{
    private static InRoomState instance;
    public static InRoomState getInstance() {
        if (instance == null) {
            instance = new InRoomState();
        }
        return instance;
    }
    @Override
    public void handle(HelloMessage helloMessage) {
        System.out.println(helloMessage.getContent()+"in room state");
    }
}
