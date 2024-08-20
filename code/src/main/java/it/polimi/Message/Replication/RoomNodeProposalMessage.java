package it.polimi.Message.Replication;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RoomNodeProposalMessage extends Message implements Serializable {
    private final ConcurrentHashMap<String, List<String>> roomsMap;
    private final Set<String> deletedRooms;
    public RoomNodeProposalMessage(ConcurrentHashMap<String, List<String>> roomsMap, Set<String> deletedRooms) {
        super(null);
        this.roomsMap = roomsMap;
        this.deletedRooms = deletedRooms;
    }

    @Override
    public void process(RoomState state) {
        ReplicationManager.getInstance().getRoomsMap().putAll(roomsMap);
        ReplicationManager.getInstance().getDeletedRooms().addAll(deletedRooms);
    }
}
