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

    public String getSender() {
        return sender;
    }
    @Override
    public void process(RoomState state) {
        // if I'm the leader in the other partitions I will respond unless I'm trying to solve the partition too
        if (NodeHistoryManager.getInstance().getS_room().tryAcquire()) {
            if (ReplicationManager.getInstance().getRoomNodes().get(0).equals(RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort())) {
                if (!NodeHistoryManager.getInstance().getSolvingPartition()) {
                    SendListRoomNodesMessage message = new SendListRoomNodesMessage(ReplicationManager.getInstance().getRoomNodes());
                    message.sendMessage(new Participant(0, "-", sender));
                }
            }else{
                String leader = ReplicationManager.getInstance().getRoomNodes().get(0);
                this.sendMessage(new Participant(0, "-", leader));
            }
        }
    }
}
