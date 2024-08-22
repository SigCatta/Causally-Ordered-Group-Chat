package it.polimi.Message.RoomNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.StableStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomsListMessage extends Message implements Serializable {
    private final Map<String, List<String>> roomsFound;

    public RoomsListMessage(Map<String, List<String>> rooms) {
        super(null);
        this.roomsFound = rooms;
    }

    @Override
    public void process(RoomState state) {
        List<String> roomsPresent = StableStorage.getInstance().getRoomNames();
        roomsFound.forEach((room, users) -> {
            if (!roomsPresent.contains(room)) {
                List<Participant> participants = new ArrayList<>();
                for (int i = 0; i < users.size(); i++) {
                    participants.add(new Participant(i, users.get(i), null));
                }
                StableStorage.getInstance().initNewRoom(room, participants);
            }
        });
    }
}
