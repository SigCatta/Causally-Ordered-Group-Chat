package it.polimi.Message.UserNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.Message.NewRoomMessage;
import it.polimi.States.RoomState;
import it.polimi.Storage.StableStorage;

import java.io.Serializable;

public class UserAddressResponseMessage extends Message implements Serializable {
    private final Participant participant;
    private final String roomName;
    private final boolean creatingRoom;

    public UserAddressResponseMessage(Participant participant, String roomName, boolean creatingRoom) {
        super(null);
        this.participant = participant;
        this.roomName = roomName;
        this.creatingRoom = creatingRoom;
    }

    @Override
    public void process(RoomState state) {
        StableStorage.getInstance().updateParticipantIp(roomName, participant);
        if (creatingRoom) {
            new NewRoomMessage(roomName, StableStorage.getInstance().getParticipants(roomName))
                    .sendMessage(participant);
        }
    }
}