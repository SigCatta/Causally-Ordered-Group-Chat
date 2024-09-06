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
    private static Set<String> roomNodes;
    private static Set<String> userNodes;
    private static NodeHistoryManager instance;

    private static Boolean solvingPartitionUser = false;
    private static Boolean solvingPartitionRoom = false;

    private final Semaphore s_user = new Semaphore(1);
    private final Semaphore s_room = new Semaphore(1);

    private NodeHistoryManager() {
        roomNodes = new HashSet<>();
        userNodes = new HashSet<>();
    }

    public static NodeHistoryManager getInstance() {
        if (instance == null) instance = new NodeHistoryManager();
        return instance;
    }

    //
    // ROOM NODES
    //

    public Set<String> getRoomNodes() {
        return roomNodes;
    }

    public void setRoomNodes(Set<String> roomNodes) {
        NodeHistoryManager.roomNodes.addAll(roomNodes);
    }

    public void addRoomNode(String node) {
        roomNodes.add(node);
    }

    public Semaphore getS_room() {
        return s_room;
    }

    public Boolean getSolvingPartitionRoom() {
        return solvingPartitionRoom;
    }

    //
    // USER NODES
    //

    public Set<String> getUserNodes() {
        return userNodes;
    }

    public void setUserNodes(Set<String> userNodes) {
        NodeHistoryManager.userNodes.addAll(userNodes);
    }


    public void addUserNode(String node) {
        userNodes.add(node);
    }

    public Semaphore getS_user() {
        return s_user;
    }

    public Boolean getSolvingPartitionUser() {
        return solvingPartitionUser;
    }


    public static void resolveUserNodesPartition() {
        RoomStateManager.getInstance().setConnected(true);
        // checking if I am the leader to solve the partition
        if (ReplicationManager.getInstance().getUserNodes().getFirst().equals(RoomStateManager.getInstance().getMyEndpoint())) {
            System.out.println("Solving user partition...");
            System.out.println(userNodes);
            solvingPartitionUser = true;
            userNodes.stream().filter(node -> !ReplicationManager.getInstance().getUserNodes().contains(node))
                    .forEach(node -> {
                        System.out.println("sending to node " + node);
                        RoomStateManager.getInstance().setConnected(true);
                        new GetListUserNodesMessage(RoomStateManager.getInstance().getMyEndpoint())
                                .sendMessage(new Participant(0, "-", node));
                    });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            solvingPartitionUser = false;
        }
    }


    public static void resolveRoomNodesPartition() {
        RoomStateManager.getInstance().setConnected(true);
        // checking if I am the leader to solve the partition
        if (ReplicationManager.getInstance().getRoomNodes().getFirst().equals(RoomStateManager.getInstance().getMyEndpoint())) {
            System.out.println("Solving room partition...");
            System.out.println(roomNodes);
            solvingPartitionRoom = true;
            roomNodes.stream().filter(node -> !ReplicationManager.getInstance().getRoomNodes().contains(node))
                    .forEach(node -> {
                        System.out.println("sending to node " + node);
                        RoomStateManager.getInstance().setConnected(true);
                        new GetListRoomNodesMessage(RoomStateManager.getInstance().getMyEndpoint())
                                .sendMessage(new Participant(0, "-", node));
                    });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            solvingPartitionRoom = false;
        }
    }

    public void newUserList(List<String> newNodes) {
        System.out.println("newUserList");
        if (ReplicationManager.getInstance().getUserNodes().getFirst().equals(RoomStateManager.getInstance().getMyEndpoint())) {
            RingUpdateMessage.broadcast(new RingUpdateMessage(null, newNodes));
            solvingPartitionUser = false;
            s_user.release();
        }
    }

    public void newRoomList(List<String> newNodes) {
        System.out.println("newRoomList");
        if (ReplicationManager.getInstance().getRoomNodes().getFirst().equals(RoomStateManager.getInstance().getMyEndpoint())) {
            RingUpdateMessage.broadcast(new RingUpdateMessage(newNodes, null));
            solvingPartitionRoom = false;
            s_room.release();
        }
    }


}


