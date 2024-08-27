package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Participant;
import it.polimi.Message.Chat.DeleteMessage;
import it.polimi.Message.RoomNodes.DeleteMessageNode;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.States.HomeState;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.util.List;

public class DeleteRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == InRoomState.getInstance()){
            String roomName = RoomStateManager.getInstance().getRoomName();
            int x = roomName.charAt(0)-97;
            StableStorage storage = StableStorage.getInstance();
            DeleteMessage message = new DeleteMessage(roomName);
            List<Participant> participants = storage.getParticipants(roomName);

            List<String> roomNodes = ReplicationManager.getInstance().getRoomNodes();
            String address = roomNodes.get(x);
            DeleteMessageNode m = new DeleteMessageNode(roomName);
            m.sendMessage(new Participant(0,"x",address));

            RoomStateManager.getInstance().setCurrentState(HomeState.getInstance());

            String myEndpoint = RoomStateManager.getInstance().getIp()+":"+RoomStateManager.getInstance().getPort();
            participants.stream()
                    .filter(participant -> !participant.name().equals(RoomStateManager.getInstance().getUsername()))
                    .forEach(p -> new GetUserAddressMessage(p, myEndpoint, roomName, false, message)
                            .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getUserNodes().get(p.name().charAt(0) - 'a')))
                    );
            storage.delete(roomName);
        } else System.out.println("you must enter the room in order to delete it");
    }
}
