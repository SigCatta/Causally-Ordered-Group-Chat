package it.polimi.Message.RoomNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.io.Serializable;

public class RoomDeletionMessage extends Message implements Serializable {
    private final String roomName;
    public RoomDeletionMessage(String roomName) {
        super(null);
        this.roomName = roomName;
    }

    @Override
    public void process(RoomState state) {
        StableStorage.getInstance().delete(roomName);
    }

    @Override
    public void handleException(Participant participant) {
        substituteFailedRoomNode(
                ReplicationManager.getInstance().getRoomNodes()
                        .get(participant.name().charAt(0) - 'a')
        );
    }
}
