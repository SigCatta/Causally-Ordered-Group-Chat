package it.polimi.Message;

import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RoomNodeProposalMessage extends Message implements Serializable {
    private final ConcurrentHashMap<String, List<String>> roomsMap;
    public RoomNodeProposalMessage(ConcurrentHashMap<String, List<String>> roomsMap) {
        super(null);
        this.roomsMap = roomsMap;
    }

    @Override
    public void process(RoomState state) {
        ReplicationManager.getInstance().getRoomsMap().putAll(roomsMap);
    }
}
