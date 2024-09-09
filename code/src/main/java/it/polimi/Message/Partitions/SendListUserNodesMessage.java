package it.polimi.Message.Partitions;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.NodeHistoryManager;

import java.io.Serializable;
import java.util.List;

public class SendListUserNodesMessage extends Message implements Serializable {
    private final List<String> UserNodes;

    public SendListUserNodesMessage(List<String> userNodes) {
        super("send list user nodes");
        UserNodes = userNodes;
    }

    @Override
    public void process(RoomState state) {
        try {
            if (NodeHistoryManager.getInstance().getS_user().tryAcquire()) {
                System.out.println("Received user nodes from leader, substituting...");
                NodeHistoryManager.getInstance().newUserList(UserNodes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
