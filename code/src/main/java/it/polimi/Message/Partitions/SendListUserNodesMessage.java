package it.polimi.Message.Partitions;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;

import java.io.Serializable;
import java.util.List;

public class SendListUserNodesMessage extends Message implements Serializable {
    private final List<String> UserNodes;

    public SendListUserNodesMessage(List<String> userNodes) {
        super("send list user nodes");
        UserNodes = userNodes;
    }

    public List<String> getUserNodes() {
        return UserNodes;
    }
    @Override
    public void process(RoomState state) {

    }
}
