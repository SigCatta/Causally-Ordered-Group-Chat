package it.polimi.Message.Replication;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.NodeHistoryManager;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class RingDataResponseMessage extends Message implements Serializable {

    private final List<String> userNodesList;
    private final List<String> roomNodesList;
    private final Set<String> roomNodesSet;
    private final Set<String> userNodesSet;

    public RingDataResponseMessage(List<String> userNodes, List<String> roomNodes, Set<String> userNodesSet, Set<String> roomNodesSet) {
        super(null);
        this.userNodesList = userNodes;
        this.roomNodesList = roomNodes;
        this.userNodesSet = userNodesSet;
        this.roomNodesSet = roomNodesSet;
    }

    @Override
    public void process(RoomState state) {
        ReplicationManager.getInstance().setRoomNodes(roomNodesList);
        ReplicationManager.getInstance().setUserNodes(userNodesList);

        NodeHistoryManager.getInstance().setRoomNodes(roomNodesSet);
        NodeHistoryManager.getInstance().setUserNodes(userNodesSet);
    }
}
