package it.polimi.Message.RoomNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;

public class DeleteNodeMessage extends Message implements Serializable {
    private final String roomName;

    public DeleteNodeMessage(String roomName){
        super("You have a room to delete!");
        this.roomName = roomName;
    }

    @Override
    public void process(RoomState state) {
        state.handle(this);
    }

    @Override
    public void handleException(Participant participant) {
        ReplicationManager.getInstance().deleteRoom(roomName);
        substituteFailedRoomNode(participant.ipAddress());
    }

    public String getRoomName() {
        return roomName;
    }
}
