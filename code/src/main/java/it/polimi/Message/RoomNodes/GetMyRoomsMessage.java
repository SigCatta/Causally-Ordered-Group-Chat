package it.polimi.Message.RoomNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class GetMyRoomsMessage extends Message implements Serializable {
    private final String username;
    private final String endpoint;

    public GetMyRoomsMessage(String username, String endpoint) {
        super(null);
        this.username = username;
        this.endpoint = endpoint;
    }

    @Override
    public void process(RoomState state) {
        HashMap<String, List<String>> roomsFound = ReplicationManager.getInstance()
                .getRoomsMap()
                .entrySet().stream()
                .filter(e -> e.getValue().contains(username)) // only include rooms where the user is present
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll); // create a new HashMap out of the results

        if (!roomsFound.isEmpty()) {
            new RoomsListMessage(roomsFound).sendMessage(new Participant(0, "-", endpoint));
        }
    }
}
