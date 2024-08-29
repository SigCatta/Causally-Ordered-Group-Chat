package it.polimi.Message.Chat;

import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.StableStorage;

import java.io.Serializable;
import java.util.List;

public class UpdateChatReplyMessage extends Message implements Serializable {

    private final String roomName;
    private final List<it.polimi.Entities.Message> chatMessages;

    public UpdateChatReplyMessage(String roomName, List<it.polimi.Entities.Message> chatMessages) {
        super("UpdateChatReplyMessage");
        this.roomName = roomName;
        this.chatMessages = chatMessages;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public void process(RoomState state) {
        List<VectorClock> vectorClocks = StableStorage.getInstance().getChatMessages(roomName).stream().map(it.polimi.Entities.Message::vectorClock).toList();
        for (it.polimi.Entities.Message message : chatMessages) {
            if (!vectorClocks.contains(message.vectorClock())){
                StableStorage.getInstance().delayMessage(roomName, message);
            }
        }

        StableStorage.getInstance().deliverDelayedMessages(roomName);
    }

    @Override
    public void handleException(Participant participant) {
    }
}
