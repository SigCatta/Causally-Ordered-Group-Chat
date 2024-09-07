package it.polimi.Message.Partitions;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.NodeHistoryManager;

import java.util.List;
import java.io.Serializable;

public class SendListRoomNodesMessage extends Message implements Serializable {
    private final List<String> roomNodes;

    public SendListRoomNodesMessage(List<String> roomNodes) {
        super("sending list room nodes");
        this.roomNodes = roomNodes;
    }
    @Override
    public void process(RoomState state) {
        try{
            if(NodeHistoryManager.getInstance().getS_room().tryAcquire()){
                System.out.println("Received room nodes from leader, substituting...");
                NodeHistoryManager.getInstance().newRoomList(roomNodes);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
