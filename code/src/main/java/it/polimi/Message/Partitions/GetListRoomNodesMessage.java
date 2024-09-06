package it.polimi.Message.Partitions;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.NodeHistoryManager;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;

public class GetListRoomNodesMessage extends Message implements Serializable {
    private final String sender;

    public GetListRoomNodesMessage(String sender) {
        super("get list room nodes");
        this.sender = sender;
    }

    @Override
    public void process(RoomState state) {
        System.out.println("Received request for room nodes");
        // if I'm the leader in the other partitions I will respond unless I'm trying to solve the partition too
        String leader = ReplicationManager.getInstance().getRoomNodes().getFirst();
        if (leader.equals(RoomStateManager.getInstance().getMyEndpoint())) {
            System.out.println("R Sending: " + ReplicationManager.getInstance().getRoomNodes());
            if (!NodeHistoryManager.getInstance().getSolvingPartitionRoom()) {
                new SendListRoomNodesMessage(ReplicationManager.getInstance().getRoomNodes())
                        .sendMessage(new Participant(0, "-", sender));
            }
        } else { // else I redirect the message to the leader
            if (leader.equals(sender)) return; // this mean that there is no actual partition, I am just not part of the ring
            System.out.println("Redirecting request to leader");
            this.sendMessage(new Participant(0, "-", leader));
        }
    }
}
