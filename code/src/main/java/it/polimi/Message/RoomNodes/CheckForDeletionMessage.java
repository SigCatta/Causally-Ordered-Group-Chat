package it.polimi.Message.RoomNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;

public class CheckForDeletionMessage extends Message implements Serializable {
    private final String roomName;
    private final String endpoint;
    public CheckForDeletionMessage(String roomName, String endpoint) {
        super(null);
        this.roomName = roomName;
        this.endpoint = endpoint;
    }

    @Override
    public void process(RoomState state) {
        if (ReplicationManager.getInstance().getDeletedRooms().contains(roomName)){
            new RoomDeletionMessage(roomName)
                    .sendMessage(new Participant(0, "-", endpoint));
        }
    }
}
