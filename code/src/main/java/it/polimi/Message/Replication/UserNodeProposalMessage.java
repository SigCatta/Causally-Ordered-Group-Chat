package it.polimi.Message.Replication;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class UserNodeProposalMessage extends Message implements Serializable {
    private final ConcurrentHashMap<String, String> usersMap;

    public UserNodeProposalMessage(ConcurrentHashMap<String, String> usersMap) {
        super(null);
        this.usersMap = usersMap;
    }

    @Override
    public void process(RoomState state) {
        ReplicationManager.getInstance().getUsersMap().putAll(usersMap);
    }

    @Override
    public void handleException(Participant participant) {
        ReplicationManager.getInstance().getUsersMap().putAll(usersMap);
    }
}
