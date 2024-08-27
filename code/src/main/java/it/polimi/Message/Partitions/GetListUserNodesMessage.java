package it.polimi.Message.Partitions;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

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
        SendListUserNodesMessage message = new SendListUserNodesMessage(ReplicationManager.getInstance().getUserNodes());
        message.sendMessage(new Participant(0, "-", sender));
    }
}
