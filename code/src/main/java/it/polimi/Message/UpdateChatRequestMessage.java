package it.polimi.Message;

import it.polimi.Entities.VectorClock;
import it.polimi.States.RoomState;

import java.io.Serializable;
import java.util.List;

public class UpdateChatRequestMessage extends Message implements Serializable {
    private final String roomName;
    private final String sender;
    private final VectorClock vc;
    private final List<it.polimi.Entities.Message> unsentMessages;

    public UpdateChatRequestMessage(String roomName, String sender, VectorClock vc, List<it.polimi.Entities.Message> unsentMessages) {
        super("update chat request");
        this.roomName = roomName;
        this.sender = sender;
        this.vc = vc;
        this.unsentMessages = unsentMessages;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getSender() {
        return sender;
    }

    public VectorClock getVectorClock() {
        return vc;
    }
    public List<it.polimi.Entities.Message> getUnsentMessages() {
        return unsentMessages;
    }
    @Override
    public void process(RoomState state) {
        state.handle(this);
    }
}
