package it.polimi.Message.Replication;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.List;

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
                int myFirst = ReplicationManager.getInstance().getRoomNodes().indexOf(myEndpoint);
                new RoomNodeProposalMessage(
                        ReplicationManager.getInstance().getRoomsMap(),
                        ReplicationManager.getInstance().getDeletedRooms()
                ).sendMessage(new Participant(0, "-", roomNodes.get(myFirst)));
            }
            for (int i = 0; i < roomNodes.size(); i++) {
                if (roomNodes.get(i) != null) ReplicationManager.getInstance().updateRoomNode(roomNodes.get(i), i);
            }
        }

        if (userNodes != null) {
            if (!userNodes.contains(myEndpoint) && ReplicationManager.getInstance().getUserNodes().contains(myEndpoint)) { // I have been substituted, I send my data to the new node
                int myFirst = ReplicationManager.getInstance().getUserNodes().indexOf(myEndpoint);
                new UserNodeProposalMessage(
                        ReplicationManager.getInstance().getUsersMap()
                ).sendMessage(new Participant(0, "-", userNodes.get(myFirst)));
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
