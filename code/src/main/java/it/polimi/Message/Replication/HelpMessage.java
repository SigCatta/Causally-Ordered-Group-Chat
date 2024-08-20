package it.polimi.Message.Replication;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HelpMessage extends Message implements Serializable {

    private final boolean room;
    private final boolean user;
    private final String ip;
    private final int port;

    public HelpMessage(boolean room, boolean user) {
        super(null);
        this.room = room;
        this.user = user;
        this.ip = RoomStateManager.getInstance().getIp();
        this.port = RoomStateManager.getInstance().getPort();
    }

    @Override
    public void process(RoomState state) {
        String myEntry = RoomStateManager.getInstance().getIp() + ':' + RoomStateManager.getInstance().getPort();

        if (room) {
            // check how many letters I handle
            List<String> roomNodes = ReplicationManager.getInstance().getRoomNodes();
            long count = roomNodes.stream()
                    .filter(myEntry::equals)
                    .count();

            if (count > 1) {
                // Add the first half of the letters I control to the new map
                int lastCharToConcede = roomNodes.indexOf(myEntry) + (int) count / 2 + 'a';

                ConcurrentHashMap<String, List<String>> mapToSend = new ConcurrentHashMap<>();
                ReplicationManager.getInstance().getRoomsMap()
                        .forEach((key, value) -> {
                            if (key.charAt(0) < lastCharToConcede) {
                                mapToSend.put(key, value);
                            }
                        });

                // send the new map to the candidate node

                try {
                    new RoomNodeProposalMessage(mapToSend, ReplicationManager.getInstance().getDeletedRooms())
                            .sendMessage(new Participant(0, "-", ip + ':' + port));
                } catch (Exception e) {
                    System.out.println("Error sending RoomNodeProposalMessage, ignoring help message...");
                    return;
                }

                // tell other ring nodes about the change
                String[] roomNodesArr = ReplicationManager.getInstance().getRoomNodes().toArray(new String[26]);
                for (int i = roomNodes.indexOf(myEntry); i < roomNodes.indexOf(myEntry) + count / 2; i++) {
                    roomNodesArr[i] = ip + ':' + port;
                }
                Message message = new UpdateRingsMessage(roomNodesArr, null);
                Arrays.stream(roomNodesArr)
                        .distinct()
                        .forEach(ip -> message.sendMessage(new Participant(0, "-", ip)));

                // remove the first half of the letters from my map ~ this is done at the very end to prevent losing data
                mapToSend.keySet().forEach(
                        key -> ReplicationManager.getInstance()
                                .getRoomsMap().remove(key)
                );
            }
        }

        if (user) {
            // check how many letters I handle
            List<String> userNodes = ReplicationManager.getInstance().getUserNodes();
            long count = userNodes.stream()
                    .filter(myEntry::equals)
                    .count();

            if (count > 1) {
                // Add the first half of the letters I control to the new map
                int lastCharToConcede = userNodes.indexOf(myEntry) + (int) count / 2 + 'a';

                ConcurrentHashMap<String, String> mapToSend = new ConcurrentHashMap<>();
                ReplicationManager.getInstance().getUsersMap()
                        .forEach((key, value) -> {
                            if (key.charAt(0) < lastCharToConcede) {
                                mapToSend.put(key, value);
                            }
                        });

                // send the new map to the candidate node
                try {
                    new UserNodeProposalMessage(mapToSend).sendMessage(new Participant(0, "-", ip + ':' + port));
                } catch (Exception e) {
                    System.out.println("Error sending UserNodeProposalMessage, ignoring help message...");
                    return;
                }

                // tell other ring nodes about the change
                String[] userNodesArr = ReplicationManager.getInstance().getRoomNodes().toArray(new String[26]);
                for (int i = userNodes.indexOf(myEntry); i < userNodes.indexOf(myEntry) + count / 2; i++) {
                    userNodesArr[i] = ip + ':' + port;
                }
                Message message = new UpdateRingsMessage(null, userNodesArr);
                Arrays.stream(userNodesArr)
                        .distinct()
                        .forEach(ip -> message.sendMessage(new Participant(0, "-", ip)));

                // remove the first half of the letters from my map ~ this is done at the very end to prevent losing data
                mapToSend.keySet().forEach(
                        key -> ReplicationManager.getInstance()
                                .getUsersMap().remove(key)
                );
            }
        }
    }
}
