package it.polimi.Message.Replication;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.NodeHistoryManager;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
        String myEndpoint = RoomStateManager.getInstance().getMyEndpoint();

        if (roomNodes != null) {
            for (int i = 0; i < roomNodes.size(); i++) {
                if (roomNodes.get(i) != null) {
                    NodeHistoryManager.getInstance().addRoomNode(roomNodes.get(i));
                    String oldNode = ReplicationManager.getInstance().getRoomNodes().get(i);

                    if (oldNode.equals(myEndpoint) && !roomNodes.get(i).equals(myEndpoint)) { // If I got substituted, I send my data over
                        int finalI = i;

                        new RoomNodeProposalMessage(
                                ReplicationManager.getInstance().getRoomsMap().entrySet().stream()
                                        .filter(e -> e.getKey().charAt(0) - 'a' == finalI)
                                        .collect(ConcurrentHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), ConcurrentHashMap::putAll),
                                ReplicationManager.getInstance().getDeletedRooms().stream().
                                        filter(r -> r.charAt(0) - 'a' == finalI)
                                        .collect(Collectors.toSet())
                        ).sendMessage(new Participant(0, "-", roomNodes.get(i)));
                    }
                    ReplicationManager.getInstance().updateRoomNode(roomNodes.get(i), i);
                }
            }
        }

        if (userNodes != null) {
            for (int i = 0; i < userNodes.size(); i++) {
                if (userNodes.get(i) != null) {
                    NodeHistoryManager.getInstance().addUserNode(userNodes.get(i));
                    String oldNode = ReplicationManager.getInstance().getUserNodes().get(i);

                    if (oldNode.equals(myEndpoint) && !userNodes.get(i).equals(myEndpoint)) { // If I got substituted, I send my data over
                        int finalI = i;

                        new UserNodeProposalMessage(
                                ReplicationManager.getInstance().getUsersMap().entrySet().stream()
                                        .filter(e -> e.getKey().charAt(0) - 'a' == finalI)
                                        .collect(ConcurrentHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), ConcurrentHashMap::putAll)
                        ).sendMessage(new Participant(0, "-", userNodes.get(i)));
                    }
                    ReplicationManager.getInstance().updateUserNode(userNodes.get(i), i);
                }
            }
        }

        if (ReplicationManager.getInstance().getUsersMap() != null)
            ReplicationManager.getInstance().getUsersMap()
                    .entrySet().stream()
                    .filter(e -> !ReplicationManager.getInstance().getUserNodes().contains(e.getValue()))
                    .forEach(e -> this.sendMessage(new Participant(0, "-", e.getValue())));

    }

    public static void broadcast(Message message) {
        List.copyOf((ReplicationManager.getInstance().getUserNodes())).stream()
                .distinct()
                .filter(s -> !s.equals(RoomStateManager.getInstance().getMyEndpoint()))
                .forEach(n -> message.sendMessage(new Participant(0, "-", n)));
    }
}
