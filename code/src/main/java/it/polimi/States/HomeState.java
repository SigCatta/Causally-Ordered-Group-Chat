package it.polimi.States;

import it.polimi.Entities.Message;
import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.*;
import it.polimi.Storage.StableStorage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
        StableStorage storage = new StableStorage();
        storage.initNewRoom(message.getRoomName(), message.getParticipants());
    }

    @Override
    public void handle(DeleteMessage message) {
        System.out.println("NOTIFICATION : "+message.getContent());
        StableStorage storage = new StableStorage();
        storage.delete(message.getRoomName());
    }

    @Override
    public void handle(ChatMessage message) {
        StableStorage storage = new StableStorage();
        VectorClock vectorClock = storage.getCurrentVectorClock(message.getRoomName());
        int index =storage.getIndex(message.getRoomName(),message.getSender());
        if(message.getMessage().vectorClock().canBeDeliveredAfter(vectorClock)){
            storage.deliverMessage(message.getRoomName(),message.getMessage());
            System.out.println("NOTIFICATION : "+message.getContent());
        }else{
            storage.delayMessage(message.getRoomName(),message.getMessage());
        }
    }

    @Override
    public void handle(UpdateChatRequestMessage message) {
        StableStorage storage = new StableStorage();
        VectorClock vectorClock = storage.getCurrentVectorClock(message.getRoomName());
        // getting unsent messages from the user just connected
        for(it.polimi.Entities.Message msg : message.getUnsentMessages()){
            if(msg.vectorClock().canBeDeliveredAfter(vectorClock)){
                storage.deliverMessage(message.getRoomName(),msg);
                msg.vectorClock().merge(vectorClock);
            }else{
                storage.delayMessage(message.getRoomName(),msg);
                msg.vectorClock().merge(vectorClock);
            }
        }
        // sending its vector clock and the chat messages
        List<Message> chatmessages = storage.getChatMessages(message.getRoomName());
        UpdateChatReplyMessage replyMessage = new UpdateChatReplyMessage(message.getRoomName(),RoomStateManager.getInstance().getUsername(),vectorClock,chatmessages);
        Participant p = storage.getParticipant(message.getRoomName(),message.getSender());
        replyMessage.sendMessage(p);
    }

}
