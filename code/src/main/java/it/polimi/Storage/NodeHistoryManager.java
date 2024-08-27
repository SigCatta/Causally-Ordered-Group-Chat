package it.polimi.Storage;

import it.polimi.Entities.Participant;
import it.polimi.Message.Partitions.GetListRoomNodesMessage;
import it.polimi.Message.Partitions.GetListUserNodesMessage;
import it.polimi.Message.Replication.RingUpdateMessage;
import it.polimi.States.RoomStateManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class NodeHistoryManager {
    private final Set<String> roomNodes;
    private final Set<String> userNodes;
    private static NodeHistoryManager instance;

    private Boolean solvingPartition;

    private final Semaphore s_user = new Semaphore(1);
    private final Semaphore s_room = new Semaphore(1);

    private NodeHistoryManager() {
        this.roomNodes = new HashSet<>();
        this.userNodes = new HashSet<>();
        this.instance = new NodeHistoryManager();
    }

    public static NodeHistoryManager getInstance() {
        if (instance == null) return new NodeHistoryManager();
        return instance;
    }

    //
    // ROOM NODES
    //

    public Set<String> getRoomNodes() {
        return roomNodes;
    }

    public void setRoomNodes(Set<String> roomNodes) {
        this.roomNodes.addAll(roomNodes);
    }


    public void addRoomNode(String node) {
        roomNodes.add(node);
    }

    public void removeRoomNode(String node) {
        roomNodes.remove(node);
    }
    public Semaphore getS_room() {
        return s_room;
    }

    //
    // USER NODES
    //

    public Set<String> getUserNodes() {
        return userNodes;
    }

    public void setUserNodes(Set<String> userNodes) {
        this.userNodes.addAll(userNodes);
    }


    public void addUserNode(String node) {
        userNodes.add(node);
    }

    public void removeUserNode(String node) {
        userNodes.remove(node);
    }

    public Semaphore getS_user() {
        return s_user;
    }

    public Boolean getSolvingPartition() {
        return solvingPartition;
    }

    public void resolveUserNodesPartition() {
        // checking if I am the leader to solve the partition
        solvingPartition = true;
        if (ReplicationManager.getInstance().getUserNodes().get(0).equals(RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort())) {
            userNodes.stream().filter(node -> !ReplicationManager.getInstance().getUserNodes().contains(node))
                    .forEach(node -> new GetListUserNodesMessage(RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort())
                            .sendMessage(new Participant(0, "-", node)));
        }
    }

    public void resolveRoomNodesPartition() {
        // checking if I am the leader to solve the partition
        if (ReplicationManager.getInstance().getRoomNodes().get(0).equals(RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort())) {
            roomNodes.stream().filter(node -> !ReplicationManager.getInstance().getRoomNodes().contains(node))
                    .forEach(node -> new GetListRoomNodesMessage(RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort())
                            .sendMessage(new Participant(0, "-", node)));
        }
    }

    public void newUserList(List<String> newNodes) throws InterruptedException {
        ReplicationManager.getInstance().getUserNodes().stream().forEach(node -> new RingUpdateMessage(null, newNodes).sendMessage(new Participant(0, "-", node)));
        ReplicationManager.getInstance().setUserNodes(newNodes);
        solvingPartition = false;
        s_user.release();
    }

    public void newRoomList(List<String> newNodes) throws InterruptedException {
        ReplicationManager.getInstance().getRoomNodes().stream().forEach(node -> new RingUpdateMessage(newNodes, null).sendMessage(new Participant(0, "-", node)));
        ReplicationManager.getInstance().setRoomNodes(newNodes);
        solvingPartition = false;
        s_room.release();
    }


}


