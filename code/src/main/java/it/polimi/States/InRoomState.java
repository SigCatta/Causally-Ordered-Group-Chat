package it.polimi.States;

import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.*;
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
        System.out.println(helloMessage.getContent()+" in room state");
    }

    @Override
    public void handle(NewRoomMessage message) {
        System.out.println("NOTIFICATION : "+message.getContent());
        StableStorage storage = new StableStorage();
        storage.initNewRoom(message.getRoomName(), message.getParticipants());
    }

    @Override
    public void handle(DeleteMessage message) {
        System.out.println("NOTIFICATION : "+message.getContent());
        StableStorage storage = new StableStorage();
        storage.delete(message.getRoomName());
        RoomStateManager.getInstance().setCurrentState(HomeState.getInstance());
    }

    @Override
    public void handle(ChatMessage message) {
        StableStorage storage = new StableStorage();
        VectorClock vectorClock = storage.getCurrentVectorClock(message.getRoomName());
        if(message.getMessage().vectorClock().canBeDeliveredAfter(vectorClock)){
            storage.deliverMessage(message.getRoomName(),message.getMessage());
            message.getMessage().vectorClock().merge(vectorClock);
            if(RoomStateManager.getInstance().getRoomName().equals(message.getRoomName())) {
                System.out.println(message.getMessage().text());
            }else{ System.out.println("NOTIFICATION : "+message.getContent());}
        }else{
            storage.delayMessage(message.getRoomName(),message.getMessage());
            message.getMessage().vectorClock().merge(vectorClock);
        }
    }
}
