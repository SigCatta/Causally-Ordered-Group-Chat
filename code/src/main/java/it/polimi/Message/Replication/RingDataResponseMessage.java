package it.polimi.Message.Replication;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;

public class RingDataResponseMessage extends Message implements Serializable {

    private final List<String> userNodes;
    private final List<String> roomNodes;

    public RingDataResponseMessage(List<String> userNodes, List<String> roomNodes) {
        super(null);
        this.userNodes = userNodes;
        this.roomNodes = roomNodes;
    }

    @Override
    public void process(RoomState state) {
        ReplicationManager.getInstance().setRoomNodes(roomNodes);
        ReplicationManager.getInstance().setRoomNodes(userNodes);
    }
}
