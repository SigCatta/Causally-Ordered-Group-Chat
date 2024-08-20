package it.polimi.Message.Replication;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;

public class UpdateRingsMessage extends Message implements Serializable {
    private final String[] roomNodes;
    private final String[] userNodes;

    public UpdateRingsMessage(String[] roomNodes, String[] userNodes) {
        super(null);
        this.roomNodes = roomNodes;
        this.userNodes = userNodes;
    }

    @Override
    public void process(RoomState state) {
        if (roomNodes != null) {
            for (int i = 0; i < roomNodes.length; i++) {
                if (roomNodes[i] != null) ReplicationManager.getInstance().updateRoomNode(roomNodes[i], i);
            }
        }

        if (userNodes != null) {
            for (int i = 0; i < userNodes.length; i++) {
                if (userNodes[i] != null) ReplicationManager.getInstance().updateUserNode(userNodes[i], i);
            }
        }
    }
}
