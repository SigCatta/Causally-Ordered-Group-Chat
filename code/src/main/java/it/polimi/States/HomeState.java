package it.polimi.States;

import it.polimi.Entities.VectorClock;
import it.polimi.Main;
import it.polimi.Message.Chat.ChatMessage;
import it.polimi.Message.Chat.DeleteMessage;
import it.polimi.Message.Chat.NewRoomMessage;
import it.polimi.Message.HelloMessage;
import it.polimi.Message.RoomNodes.DeleteNodeMessage;
import it.polimi.Message.RoomNodes.NewRoomNodeMessage;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

public class HomeState implements RoomState {
    private static HomeState instance;

    public static HomeState getInstance() {
        if (instance == null) {
            instance = new HomeState();
        }
        return instance;
    }

    @Override
    public void handle(HelloMessage helloMessage) {
        System.out.println(helloMessage.getContent() + " in home state");
    }

    @Override
    public void handle(NewRoomMessage message) {
        System.out.println("NOTIFICATION : " + message.getContent());
        StableStorage storage = StableStorage.getInstance();
        storage.initNewRoom(message.getRoomName(), message.getParticipants());
    }

    @Override
    public void handle(DeleteMessage message) {
        System.out.println("NOTIFICATION : " + message.getContent());
        StableStorage storage = StableStorage.getInstance();
        storage.delete(message.getRoomName());
    }

    @Override
    public void handle(ChatMessage message) {
        if (!StableStorage.getInstance().doesRoomExist(message.getRoomName())) {
            Main.startup();
            return;
        }

        StableStorage storage = StableStorage.getInstance();
        VectorClock vectorClock = storage.getCurrentVectorClock(message.getRoomName());
        if (message.getMessage().vectorClock().canBeDeliveredAfter(vectorClock)) {
            storage.deliverMessage(message.getRoomName(), message.getMessage());
            System.out.println("NOTIFICATION : " + message.getContent());
        } else {
            storage.delayMessage(message.getRoomName(), message.getMessage());
        }
        storage.deliverDelayedMessages(message.getRoomName());
    }


    @Override
    public void handle(NewRoomNodeMessage message) {
        ReplicationManager.getInstance().addRoom(message.getRoomName(), message.getParticipants());
    }

    @Override
    public void handle(DeleteNodeMessage message) {
        ReplicationManager.getInstance().deleteRoom(message.getRoomName());
    }

}
