package it.polimi.Message;

import it.polimi.Entities.VectorClock;
import it.polimi.States.RoomState;

import java.io.Serializable;
import java.util.List;

public class UpdateChatReplyMessage extends Message implements Serializable {

    private final String roomName;
    private final String sender;
    private final VectorClock vectorClock;
    private final List<it.polimi.Entities.Message> chatmessages;

    public UpdateChatReplyMessage(String roomName, String sender, VectorClock vectorClock, List<it.polimi.Entities.Message> chatmessages) {
        super("UpdateChatReplyMessage");
        this.roomName = roomName;
        this.sender = sender;
        this.vectorClock = vectorClock;
        this.chatmessages = chatmessages;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getSender() {
        return sender;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public List<it.polimi.Entities.Message> getChatmessages() {
        return chatmessages;
    }
    @Override
    public void process(RoomState state) {

    }
}