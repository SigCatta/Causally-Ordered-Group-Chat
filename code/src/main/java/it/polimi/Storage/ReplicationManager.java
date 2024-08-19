package it.polimi.Storage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ReplicationManager {
    private static ReplicationManager instance;
    private final ConcurrentHashMap<String, String> usernamesMap;
    private final ConcurrentHashMap<String, List<String>> roomsMap;
    private final List<String> deletedRoomsList;

    private final String[] roomNodes;
    private final String[] userNodes;

    private ReplicationManager() {
        this.usernamesMap = new ConcurrentHashMap<>();
        this.roomsMap = new ConcurrentHashMap<>();
        this.deletedRoomsList = new ArrayList<>();

        this.roomNodes = new String[26];
        this.userNodes = new String[26];
    }

    public static ReplicationManager getInstance() {
        if (instance == null) {
            instance = new ReplicationManager();
        }
        return instance;
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

    public String[] getRoomNodes() {
        return roomNodes;
    }

    public String[] getUserNodes() {
        return userNodes;
    }

    public void updateRoomNode(String node, int index) {
        roomNodes[index] = node;
    }

    public void updateUserNode(String node, int index) {
        userNodes[index] = node;
    }

    public void setRoomNodes(String[] roomNodes) {
        System.arraycopy(roomNodes, 0, this.roomNodes, 0, roomNodes.length);
    }

    public void setUserNodes(String[] userNodes) {
        System.arraycopy(userNodes, 0, this.userNodes, 0, userNodes.length);
    }

    //
    // ROOMS NODE
    //

    // Returns the node that is handling the most rooms
    public String chooseRoomNodeToHelp() {
        return Arrays.stream(roomNodes)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(node -> node, Collectors.summingInt(node -> 1)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

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

    // Returns the node that is handling the most usernames
    public String chooseUserNodeToHelp() {
        return Arrays.stream(userNodes)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(node -> node, Collectors.summingInt(node -> 1)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Adds a user or updates its ip address
    public void addUser(String username, String ipAddress) {
        usernamesMap.put(username, ipAddress);
    }

    // Returns the ip address of a user
    public String getIpAddress(String username) {
        return usernamesMap.get(username);
    }
}
