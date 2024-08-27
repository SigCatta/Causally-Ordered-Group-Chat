package it.polimi.Message.Replication;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RingUpdateMessage extends Message implements Serializable {
    private final List<String> roomNodes;
    private final List<String> userNodes;

    public RingUpdateMessage(List<String> roomNodes, List<String> userNodes) {
        super(null);
        this.roomNodes = roomNodes;
        this.userNodes = userNodes;
    }

    @Override
    public void process(RoomState state) {
        String myEndpoint = RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort();

        if (roomNodes != null) {
            if (!roomNodes.contains(myEndpoint) && ReplicationManager.getInstance().getRoomNodes().contains(myEndpoint)) { // I have been substituted, I send my data to the new node
                IntStream.range(0, ReplicationManager.getInstance().getRoomNodes().size())
                        .filter(i -> ReplicationManager.getInstance().getRoomNodes().get(i).equals(myEndpoint))
                        .forEach(i ->
                                new RoomNodeProposalMessage(
                                        ReplicationManager.getInstance().getRoomsMap().entrySet().stream()
                                                .filter(e -> e.getKey().charAt(0) - 'a' == i)
                                                .collect(ConcurrentHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), ConcurrentHashMap::putAll),
                                        ReplicationManager.getInstance().getDeletedRooms().stream().
                                                filter(r -> r.charAt(0) - 'a' == i)
                                                .collect(Collectors.toSet())
                                ).sendMessage(new Participant(0, "-", roomNodes.get(i)))
                        );
            }
            for (int i = 0; i < roomNodes.size(); i++) {
                if (roomNodes.get(i) != null) ReplicationManager.getInstance().updateRoomNode(roomNodes.get(i), i);
            }
        }

        if (userNodes != null) {
            if (!userNodes.contains(myEndpoint) && ReplicationManager.getInstance().getUserNodes().contains(myEndpoint)) { // I have been substituted, I send my data to the new node
                IntStream.range(0, ReplicationManager.getInstance().getUserNodes().size())
                        .filter(i -> ReplicationManager.getInstance().getUserNodes().get(i).equals(myEndpoint))
                        .forEach(i ->
                                new UserNodeProposalMessage(
                                        ReplicationManager.getInstance().getUsersMap().entrySet().stream()
                                                .filter(e -> e.getKey().charAt(0) - 'a' == i)
                                                .collect(ConcurrentHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), ConcurrentHashMap::putAll)
                                ).sendMessage(new Participant(0, "-", userNodes.get(i)))
                        );
            }
            for (int i = 0; i < userNodes.size(); i++) {
                if (userNodes.get(i) != null) ReplicationManager.getInstance().updateUserNode(userNodes.get(i), i);
            }
        }

        if (ReplicationManager.getInstance().getUsersMap() != null)
            ReplicationManager.getInstance().getUsersMap()
                    .entrySet().stream()
                    .filter(e -> !ReplicationManager.getInstance().getUserNodes().contains(e.getValue()))
                    .forEach(e -> this.sendMessage(new Participant(0, "-", e.getValue())));
    }

    public static void broadcast(Message message) {
        ReplicationManager.getInstance().getUserNodes().stream()
                .distinct()
                .forEach(n -> message.sendMessage(new Participant(0, "-", n)));
    }

    @Override
    public void handleException(Participant participant) {
        substituteFailedUserNode(participant.ipAddress());
    }
}
