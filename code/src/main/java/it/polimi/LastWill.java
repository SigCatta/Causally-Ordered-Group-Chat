package it.polimi;

import it.polimi.Entities.Participant;
import it.polimi.Message.Replication.LastWillMessage;
import it.polimi.Message.Replication.RingUpdateMessage;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;

import java.util.List;

public class LastWill {
    public static void execute() {
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

            RingUpdateMessage ringUpdateMessage = new RingUpdateMessage((String[]) roomNodes.toArray(), null);
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

            RingUpdateMessage ringUpdateMessage = new RingUpdateMessage(null, (String[]) userNodes.toArray());
            userNodes.stream()
                    .distinct()
                    .filter(ip -> !ip.equals(myEndpoint))
                    .forEach(ip -> ringUpdateMessage.sendMessage(new Participant(0, "-", ip)));
        }
    }
}
