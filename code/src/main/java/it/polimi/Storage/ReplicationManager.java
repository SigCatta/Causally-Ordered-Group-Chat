package it.polimi.Storage;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ReplicationManager {
    private final ConcurrentHashMap<String, String> usernamesMap;
    private final ConcurrentHashMap<String, List<String>> roomsMap;
    private final List<String> deletedRoomsList;

    public ReplicationManager() {
        this.usernamesMap = new ConcurrentHashMap<>();
        this.roomsMap = new ConcurrentHashMap<>();
        this.deletedRoomsList = new ArrayList<>();
    }

    public List<String> getDeletedRoomsList() {
        return deletedRoomsList;
    }

    public ConcurrentHashMap<String, List<String>> getRoomsMap() {
        return roomsMap;
    }

    public ConcurrentHashMap<String, String> getUsernamesMap() {
        return usernamesMap;
    }

    //
    // ROOMS NODE
    //

    // Adds all data inherited from a previous rooms node
    public void becomeRoomsNode(List<String> deletedRooms, ConcurrentHashMap<String, List<String>> roomMembers) {
        this.deletedRoomsList.addAll(deletedRooms);
        this.roomsMap.putAll(roomMembers);
    }

    // Adds a new room
    public void addRoom(String roomId, List<String> usernames) {
        roomsMap.put(roomId, usernames);
    }

    // Returns the list of rooms that are not present in the given list
    public List<String> getRoomsDiff(List<String> rooms) {
        return roomsMap.keySet().stream()
                .filter(room -> !rooms.contains(room))
                .toList();
    }

    // Returns all participants from a  room
    public List<String> getParticipants(String roomId) {
        return roomsMap.get(roomId);
    }

    // Registers a room as deleted
    public void deleteRoom(String roomId) {
        roomsMap.remove(roomId);
        deletedRoomsList.add(roomId);
    }

    // Returns a list containing the rooms from the given list that are deleted
    public List<String> getDeletedRooms(List<String> rooms) {
        return deletedRoomsList.stream()
                .filter(room -> !rooms.contains(room))
                .toList();
    }

    //
    // USERS NODE
    //

    // Adds a user or updates its ip address
    public void addUser(String username, String ipAddress) {
        usernamesMap.put(username, ipAddress);
    }

    // Returns the ip address of a user
    public String getIpAddress(String username) {
        return usernamesMap.get(username);
    }
}
