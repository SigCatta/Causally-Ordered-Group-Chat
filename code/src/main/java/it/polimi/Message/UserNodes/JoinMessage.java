package it.polimi.Message.UserNodes;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;

public class JoinMessage extends Message implements Serializable {
    private final String endpoint;
    private final String username;

    public JoinMessage(String username, String endpoint) {
        super(null);
        this.username = username;
        this.endpoint = endpoint;
    }

    @Override
    public void process(RoomState state) {
        ReplicationManager.getInstance().addUser(username, endpoint);
    }
}
