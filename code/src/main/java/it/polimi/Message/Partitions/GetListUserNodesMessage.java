package it.polimi.Message.Partitions;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.NodeHistoryManager;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;

public class GetListUserNodesMessage extends Message implements Serializable {
    private final String sender;

    public GetListUserNodesMessage(String sender) {
        super("requesting list user nodes");
        this.sender = sender;
    }

    @Override
    public void process(RoomState state) {
        System.out.println("Received request for user nodes");
        // if I'm the leader in the other partitions I will respond unless I'm trying to solve the partition too
        String leader = ReplicationManager.getInstance().getUserNodes().getFirst();
        if (leader.equals(RoomStateManager.getInstance().getMyEndpoint())) {
            if (!NodeHistoryManager.getInstance().getSolvingPartitionUser()) {
                new SendListUserNodesMessage(ReplicationManager.getInstance().getUserNodes())
                        .sendMessage(new Participant(0, "-", sender));
            }
        } else {
            if (leader.equals(sender)) return; // this mean that there is no actual partition, I am just not part of the ring
            System.out.println("Redirecting request to leader");
            this.sendMessage(new Participant(0, "-", leader));
        }
    }
}
