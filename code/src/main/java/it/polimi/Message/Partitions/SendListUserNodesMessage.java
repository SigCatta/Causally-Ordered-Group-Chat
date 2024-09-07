package it.polimi.Message.Partitions;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.NodeHistoryManager;
import it.polimi.Storage.ReplicationManager;


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
        try{
            if(NodeHistoryManager.getInstance().getS_user().tryAcquire()){
                System.out.println("Received user nodes from leader, substituting...");
                NodeHistoryManager.getInstance().newUserList(UserNodes);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
