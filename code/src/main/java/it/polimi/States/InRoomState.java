package it.polimi.States;

import it.polimi.Message.HelloMessage;
import it.polimi.Message.Message;
import it.polimi.Message.NewRoomMessage;
import it.polimi.Storage.StableStorage;

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

    @Override
    public void handle(NewRoomMessage message) {
        System.out.println("NOTIFICATION : "+message.getContent());
        StableStorage storage = new StableStorage();
        storage.initNewRoom(message.getRoomName(), message.getParticipants());
    }
}
