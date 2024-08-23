package it.polimi.Message.Replication;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LastWillMessage extends Message implements Serializable {

    private final ConcurrentHashMap<String, String> usersMap;
    private final ConcurrentHashMap<String, List<String>> roomsMap;
    private final Set<String> deletedRooms;

    public LastWillMessage(ConcurrentHashMap<String, String> usersMap, ConcurrentHashMap<String, List<String>> roomsMap, Set<String> deletedRooms) {
        super(null);
        this.usersMap = usersMap;
        this.roomsMap = roomsMap;
        this.deletedRooms = deletedRooms;
    }

    @Override
    public void process(RoomState state) {
        if (usersMap != null) {
            ReplicationManager.getInstance().getUsersMap().putAll(usersMap);
        }
        if (roomsMap != null && deletedRooms != null) {
            ReplicationManager.getInstance().getRoomsMap().putAll(roomsMap);
            ReplicationManager.getInstance().getDeletedRooms().addAll(deletedRooms);
        }
    }
}
