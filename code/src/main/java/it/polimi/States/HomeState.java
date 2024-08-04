package it.polimi.States;

import it.polimi.Entities.Participant;
import it.polimi.Message.HelloMessage;
import it.polimi.Message.Message;
import it.polimi.Message.NewRoomMessage;
import it.polimi.Storage.StableStorage;

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
        System.out.println(helloMessage.getContent()+" in home state");
    }

    @Override
    public void handle(NewRoomMessage message) {
        System.out.println("NOTIFICATION : "+message.getContent());
        for(Participant p : message.getParticipants()){
            if(p.name().equals(RoomStateManager.getInstance().getUsername())){
                message.getParticipants().remove(p);
            }
        }
        StableStorage storage = new StableStorage();
        storage.initNewRoom(message.getRoomName(), message.getParticipants());
    }
}
