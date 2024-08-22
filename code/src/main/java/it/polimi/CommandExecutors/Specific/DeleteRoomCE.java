package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Participant;
import it.polimi.Message.DeleteMessage;
import it.polimi.Message.DeleteMessageNode;
import it.polimi.Message.NewRoomNodeMessage;
import it.polimi.States.HomeState;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == InRoomState.getInstance()){
            String roomName = RoomStateManager.getInstance().getRoomName();
            int x = roomName.charAt(0)-97;
            StableStorage storage = StableStorage.getInstance();
            DeleteMessage message = new DeleteMessage(roomName);
            List<Participant> participants = storage.getParticipants(roomName);
            storage.delete(roomName);
            RoomStateManager.getInstance().setCurrentState(HomeState.getInstance());
            ExecutorService executor = Executors.newFixedThreadPool(10);
            for (Participant participant : participants) {
                executor.submit(() -> {
                    if(!participant.name().equals(RoomStateManager.getInstance().getUsername())){
                        message.sendMessage(participant);
                    }});
            }
            executor.close();
            List<String> roomNodes = ReplicationManager.getInstance().getRoomNodes();
            String address = roomNodes.get(x);
            DeleteMessageNode m = new DeleteMessageNode(roomName);
            m.sendMessage(new Participant(0,"x",address));
        }
    }
}
