package it.polimi.Message.Chat;

import it.polimi.Entities.Participant;
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
        chatMessages.stream()
                .filter(message ->
                        StableStorage.getInstance().getChatMessages(roomName).stream()
                                .noneMatch(m -> m.vectorClock().equals(message.vectorClock())))
                .forEach(message -> StableStorage.getInstance().delayMessage(roomName, message));
        StableStorage.getInstance().deliverDelayedMessages(roomName);
    }

    @Override
    public void handleException(Participant participant) {
    }
}
