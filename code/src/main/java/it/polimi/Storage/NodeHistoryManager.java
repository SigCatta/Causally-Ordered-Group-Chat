package it.polimi.Storage;

import it.polimi.Entities.Participant;
import it.polimi.Message.Partitions.GetListUserNodesMessage;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.States.RoomStateManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class NodeHistoryManager {
    private final Set<String> roomNodes;
    private final Set<String> userNodes;
    private final NodeHistoryManager instance;

    private Set<String> listRoomNodes;
    private Set<String> listUserNodes;

    private Semaphore roomNodesSemaphore = new Semaphore(1);

    private NodeHistoryManager() {
        this.roomNodes = new HashSet<>();
        this.userNodes = new HashSet<>();
        this.instance = new NodeHistoryManager();
    }

    public NodeHistoryManager getInstance() {
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

    public Set<String> getListRoomNodes() {
        return listRoomNodes;
    }

    public void setListRoomNodes(Set<String> listRoomNodes) {
        this.listRoomNodes = listRoomNodes;
    }

    public void addRoomNode(String node) {
        roomNodes.add(node);
    }

    public void removeRoomNode(String node) {
        roomNodes.remove(node);
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

    public Set<String> getListUserNodes() {
        return listUserNodes;
    }

    public void setListUserNodes(Set<String> listUserNodes) {
        this.listUserNodes = listUserNodes;
    }

    public void addUserNode(String node) {
        userNodes.add(node);
    }

    public void removeUserNode(String node) {
        userNodes.remove(node);
    }

    public Semaphore getRoomNodesSemaphore() {
        return roomNodesSemaphore;
    }

    public void resolveUserNodesPartition() {
        // checking if I am the leader to solve the partition
        if (ReplicationManager.getInstance().getUserNodes().get(0).equals(RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort())) {
            userNodes.stream().filter(node -> !ReplicationManager.getInstance().getUserNodes().contains(node))
                    .forEach(node -> new GetListUserNodesMessage(RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort())
                            .sendMessage(new Participant(0, "-", node)));
        }
    }

}
