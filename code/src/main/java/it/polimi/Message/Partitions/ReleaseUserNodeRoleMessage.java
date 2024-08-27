package it.polimi.Message.Partitions;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;

import java.io.Serializable;

public class ReleaseUserNodeRoleMessage extends Message implements Serializable {
    private final String endPoint;
    public ReleaseUserNodeRoleMessage(String endPoint) {
        super("releasing user node role");
        this.endPoint = endPoint;
    }

    @Override
    public void process(RoomState state) {
        // sends your data to endPoint
    }
}
