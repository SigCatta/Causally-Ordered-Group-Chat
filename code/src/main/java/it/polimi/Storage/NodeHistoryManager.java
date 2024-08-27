package it.polimi.Storage;

import java.util.HashSet;
import java.util.Set;

public class NodeHistoryManager {
    private final Set<String> roomNodes;
    private final Set<String> userNodes;
    private final NodeHistoryManager instance;

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

    public void addUserNode(String node) {
        userNodes.add(node);
    }

    public void removeUserNode(String node) {
        userNodes.remove(node);
    }
}
