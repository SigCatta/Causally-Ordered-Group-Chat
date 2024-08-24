package it.polimi;

import it.polimi.Entities.DataContainer;
import it.polimi.Entities.Participant;
import it.polimi.Message.Replication.LastWillMessage;
import it.polimi.Message.Replication.RingUpdateMessage;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.DataSerializer;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LastWill {
    public static void execute() {
        try {
            sendLastWill();
        } catch (Exception e) {
            // If for some reason data cannot be sent, save it to disk so not to lose it
            saveDataToDisk();
        }
    }

    private static void sendLastWill() {
        String myEndpoint = RoomStateManager.getInstance().getIp() + ':' + RoomStateManager.getInstance().getPort();
        List<String> roomNodes = ReplicationManager.getInstance().getRoomNodes();
        List<String> userNodes = ReplicationManager.getInstance().getUserNodes();

        // If I am a room node, send the data
        if (roomNodes.contains(myEndpoint)) {
            String saviour;
            try {
                saviour = roomNodes.get(roomNodes.lastIndexOf(myEndpoint) + 1);
            } catch (IndexOutOfBoundsException e) {
                saviour = roomNodes.get(roomNodes.indexOf(myEndpoint) - 1);
            }

            new LastWillMessage(
                    null,
                    ReplicationManager.getInstance().getRoomsMap(),
                    ReplicationManager.getInstance().getDeletedRooms()
            ).sendMessage(new Participant(0, "-", saviour));

            for (int i = 0; i < roomNodes.size(); i++) {
                if (roomNodes.get(i).equals(myEndpoint))
                    roomNodes.set(i, saviour);
            }

            RingUpdateMessage ringUpdateMessage = new RingUpdateMessage(roomNodes, null);
            roomNodes.stream()
                    .distinct()
                    .filter(ip -> !ip.equals(myEndpoint))
                    .forEach(ip -> ringUpdateMessage.sendMessage(new Participant(0, "-", ip)));
        }

        // If I am a user node, send the data
        if (userNodes.contains(myEndpoint)) {
            String saviour;
            try {
                saviour = userNodes.get(userNodes.lastIndexOf(myEndpoint) + 1);
            } catch (IndexOutOfBoundsException e) {
                saviour = userNodes.get(userNodes.indexOf(myEndpoint) - 1);
            }

            new LastWillMessage(
                    ReplicationManager.getInstance().getUsersMap(),
                    null,
                    null
            ).sendMessage(new Participant(0, "-", saviour));

            for (int i = 0; i < userNodes.size(); i++) {
                if (userNodes.get(i).equals(myEndpoint))
                    userNodes.set(i, saviour);
            }

            RingUpdateMessage ringUpdateMessage = new RingUpdateMessage(null, userNodes);
            userNodes.stream()
                    .distinct()
                    .filter(ip -> !ip.equals(myEndpoint))
                    .forEach(ip -> ringUpdateMessage.sendMessage(new Participant(0, "-", ip)));
        }

    }

    private static void saveDataToDisk() {
        ConcurrentHashMap<String, List<String>> roomsMap = ReplicationManager.getInstance().getRoomsMap();
        Set<String> deletedRooms = ReplicationManager.getInstance().getDeletedRooms();
        ConcurrentHashMap<String, String> usersMap = ReplicationManager.getInstance().getUsersMap();

        // Serialize data to disk
        DataSerializer.serializeData(StableStorage.getInstance().getBackupPath(), new DataContainer(roomsMap, deletedRooms, usersMap));
    }
}
