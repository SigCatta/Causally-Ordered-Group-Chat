package it.polimi.Message.Partitions;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.NodeHistoryManager;
import it.polimi.Storage.ReplicationManager;
import org.w3c.dom.Node;

import java.io.Serializable;

public class GetListUserNodesMessage extends Message implements Serializable {
    private final String sender;

    public GetListUserNodesMessage(String sender) {
        super("requesting list user nodes");
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public void process(RoomState state) {
        System.out.println("ok");
        // if I'm the leader in the other partitions I will respond unless I'm trying to solve the partition too
        if (ReplicationManager.getInstance().getUserNodes().get(0).equals(RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort())) {
            System.out.println(NodeHistoryManager.getInstance().getSolvingPartitionUser());
            if (!NodeHistoryManager.getInstance().getSolvingPartitionUser()) {
                SendListUserNodesMessage message = new SendListUserNodesMessage(ReplicationManager.getInstance().getUserNodes());
                message.sendMessage(new Participant(0, "-", sender));
            }
        } else {
            String leader = ReplicationManager.getInstance().getUserNodes().get(0);
            this.sendMessage(new Participant(0, "-", leader));
        }
    }
}
