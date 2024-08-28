package it.polimi.Message.UserNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.StableStorage;

import java.io.Serializable;

public class UserAddressResponseMessage extends Message implements Serializable {
    private final Participant participant;
    private final String roomName;
    private final Message message;

    public UserAddressResponseMessage(Participant participant, String roomName, Message message) {
        super(null);
        this.participant = participant;
        this.roomName = roomName;
        this.message = message;
    }

    @Override
    public void process(RoomState state) {
        if (StableStorage.getInstance().getRoomNames().contains(roomName)) { // if the room does not exist, there is no user ip to update
            StableStorage.getInstance().updateParticipantIp(roomName, participant);
            message.sendMessage(participant);
        }
    }
}
