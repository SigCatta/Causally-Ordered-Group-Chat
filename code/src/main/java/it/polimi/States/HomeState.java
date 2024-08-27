package it.polimi.States;

import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.*;
import it.polimi.Message.Chat.*;
import it.polimi.Message.RoomNodes.DeleteNodeMessage;
import it.polimi.Message.RoomNodes.NewRoomNodeMessage;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.util.List;

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
        StableStorage storage = StableStorage.getInstance();
        storage.initNewRoom(message.getRoomName(), message.getParticipants());
    }

    @Override
    public void handle(DeleteMessage message) {
        System.out.println("NOTIFICATION : "+message.getContent());
        StableStorage storage = StableStorage.getInstance();
        storage.delete(message.getRoomName());
    }

    @Override
    public void handle(ChatMessage message) {
        StableStorage storage = StableStorage.getInstance();
        VectorClock vectorClock = storage.getCurrentVectorClock(message.getRoomName());
        int index =storage.getIndex(message.getRoomName(),message.getSender());
        if(message.getMessage().vectorClock().canBeDeliveredAfter(vectorClock)){
            storage.deliverMessage(message.getRoomName(),message.getMessage());
            System.out.println("NOTIFICATION : "+message.getContent());
        }else{
            storage.delayMessage(message.getRoomName(),message.getMessage());
        }
        storage.deliverDelayedMessages(message.getRoomName());
    }

    @Override
    public void handle(UpdateChatRequestMessage message) {
        StableStorage storage = StableStorage.getInstance();
        if(storage.getRoomNames().contains(message.getRoomName())){
            // getting unsent messages from the user just connected
            for (it.polimi.Entities.Message msg : message.getUnsentMessages()) {
                VectorClock vectorClock = storage.getCurrentVectorClock(message.getRoomName());
                if (msg.vectorClock().canBeDeliveredAfter(vectorClock)) {
                    storage.deliverMessage(message.getRoomName(), msg);
                } else {
                    storage.delayMessage(message.getRoomName(), msg);
                }
            }
            storage.deliverDelayedMessages(message.getRoomName());
            // sending its vector clock and the chat messages
            List<it.polimi.Entities.Message> chatmessages = storage.getChatMessages(message.getRoomName());
            UpdateChatReplyMessage replyMessage = new UpdateChatReplyMessage
                    (message.getRoomName(), RoomStateManager.getInstance().getUsername(), storage.getCurrentVectorClock(message.getRoomName()), chatmessages);
            Participant p = storage.getParticipant(message.getRoomName(), message.getSender());
            String myEndpoint = RoomStateManager.getInstance().getIp()+":"+RoomStateManager.getInstance().getPort();
            new GetUserAddressMessage(p, myEndpoint, message.getRoomName(), false, replyMessage)
                    .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getUserNodes().get(p.name().charAt(0) - 'a')));
        }
    }

    @Override
    public void handle(NewRoomNodeMessage message){
        ReplicationManager.getInstance().addRoom(message.getRoomName(),message.getParticipants());
    }

    @Override
    public void handle(DeleteNodeMessage message) {
        ReplicationManager.getInstance().deleteRoom(message.getRoomName());
    }

}
