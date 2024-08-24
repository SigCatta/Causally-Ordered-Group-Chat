package it.polimi.Message.Replication;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;

public class RingUpdateMessage extends Message implements Serializable {
    private final List<String> roomNodes;
    private final List<String> userNodes;

    public RingUpdateMessage(List<String> roomNodes, List<String> userNodes) {
        super(null);
        this.roomNodes = roomNodes;
        this.userNodes = userNodes;
    }

    @Override
    public void process(RoomState state) {
        if (roomNodes != null) {
            for (int i = 0; i < roomNodes.size(); i++) {
                if (roomNodes.get(i) != null) ReplicationManager.getInstance().updateRoomNode(roomNodes.get(i), i);
            }
        }

        if (userNodes != null) {
            for (int i = 0; i < userNodes.size(); i++) {
                if (userNodes.get(i) != null) ReplicationManager.getInstance().updateUserNode(userNodes.get(i), i);
            }
        }
    }
}
