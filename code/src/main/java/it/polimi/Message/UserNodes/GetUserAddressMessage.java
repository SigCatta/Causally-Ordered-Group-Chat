package it.polimi.Message.UserNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;

public class GetUserAddressMessage extends Message implements Serializable {
    private final Participant participant;
    private final String endpoint;
    private final String roomName;
    private final boolean creatingRoom;
    private final Message message;

    public GetUserAddressMessage(Participant participant, String endpoint, String roomName, boolean creatingRoom, Message message) {
        super("request of address");
        this.participant = participant;
        this.endpoint = endpoint;
        this.roomName = roomName;
        this.creatingRoom = creatingRoom;
        this.message = message;
    }

    @Override
    public void process(RoomState state) {
        String address = ReplicationManager.getInstance().getIpAddress(participant.name());
        if (address != null)
            if (!address.isEmpty()) {
                new UserAddressResponseMessage(new Participant(participant.index(), participant.name(), address), roomName, creatingRoom, message)
                        .sendMessage(new Participant(0, "-", endpoint));
            }
    }

    @Override
    public void handleException(Participant participant) {
        substituteFailedUserNode(
                ReplicationManager.getInstance().getUserNodes()
                        .get(participant.name().charAt(0) - 'a')
        );
    }
}
