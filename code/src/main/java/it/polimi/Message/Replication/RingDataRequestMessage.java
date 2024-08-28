package it.polimi.Message.Replication;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.NodeHistoryManager;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;

public class RingDataRequestMessage extends Message implements Serializable {

    private final String endpoint; // endpoint of who is requesting the data

    public RingDataRequestMessage(String endpoint) {
        super(null);
        this.endpoint = endpoint;
    }

    @Override
    public void process(RoomState state) {
        new RingDataResponseMessage(
                ReplicationManager.getInstance().getUserNodes(),
                ReplicationManager.getInstance().getRoomNodes(),
                NodeHistoryManager.getInstance().getUserNodes(),
                NodeHistoryManager.getInstance().getRoomNodes()
        ).sendMessage(new Participant(0, "-", endpoint));
    }
}
