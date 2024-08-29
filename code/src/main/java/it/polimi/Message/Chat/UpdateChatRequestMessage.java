package it.polimi.Message.Chat;

import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.StableStorage;

import java.io.Serializable;
import java.util.List;

public class UpdateChatRequestMessage extends Message implements Serializable {
    private final String roomName;
    private final String sender;
    private final List<it.polimi.Entities.Message> unsentMessages;

    public UpdateChatRequestMessage(String roomName, String sender, List<it.polimi.Entities.Message> unsentMessages) {
        super("update chat request");
        this.roomName = roomName;
        this.sender = sender;
        this.unsentMessages = unsentMessages;
    }

    @Override
    public void process(RoomState state) {
        StableStorage storage = StableStorage.getInstance();
        if (storage.getRoomNames().contains(roomName)) {
            // getting unsent messages from the user just connected
            for (it.polimi.Entities.Message msg : unsentMessages) {
                VectorClock vectorClock = storage.getCurrentVectorClock(roomName);
                if (msg.vectorClock().canBeDeliveredAfter(vectorClock)) {
                    storage.deliverMessage(roomName, msg);
                } else {
                    storage.delayMessage(roomName, msg);
                }
            }
            storage.deliverDelayedMessages(roomName);
            // sending its vector clock and the chat messages
            new UpdateChatReplyMessage(roomName, storage.getChatMessages(roomName))
                    .sendMessage(new Participant(0, "-", sender));
        }

    }

    @Override
    public void handleException(Participant participant) {
    }

}
