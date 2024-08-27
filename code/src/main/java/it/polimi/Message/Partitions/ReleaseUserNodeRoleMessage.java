package it.polimi.Message.Partitions;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;

import java.io.Serializable;

public class ReleaseUserNodeRoleMessage extends Message implements Serializable {
    public ReleaseUserNodeRoleMessage() {

        super("releasing user node role");
    }


    @Override
    public void process(RoomState state) {

    }
}
