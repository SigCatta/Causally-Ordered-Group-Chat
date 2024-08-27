package it.polimi.Message.RoomNodes;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.Message.Chat.UpdateChatRequestMessage;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;
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
            if (!roomsPresent.contains(room)) { // add new rooms
                List<Participant> participants = new ArrayList<>();
                for (int i = 0; i < users.size(); i++) {
                    participants.add(new Participant(i, users.get(i), null));
                }
                StableStorage.getInstance().initNewRoom(room, participants);

                UpdateChatRequestMessage message = new UpdateChatRequestMessage(
                        room,
                        RoomStateManager.getInstance().getUsername(),
                        StableStorage.getInstance().getCurrentVectorClock(room),
                        StableStorage.getInstance().getUnsentMessages(room)
                );

                String myEndpoint = RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort();
                StableStorage.getInstance().getParticipants(room)
                        .forEach(p -> {
                            if (p.ipAddress().equals("null")) {
                                new GetUserAddressMessage(p, myEndpoint, room, false, message)
                                        .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getUserNodes().get(p.name().charAt(0) - 'a')));
                            } else message.sendMessage(p);
                        });
            }
        });
    }
}
