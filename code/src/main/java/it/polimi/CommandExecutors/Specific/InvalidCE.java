package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Message;
import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.Chat.ChatMessage;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

public class InvalidCE implements CommandExecutor {
    private final String command;

    public InvalidCE(String command) {
        this.command = command;
    }

    @Override
    public void execute() {
        if (RoomStateManager.getInstance().getCurrentState() == InRoomState.getInstance()) {
            String text = command;
            text = RoomStateManager.getInstance().getUsername() + " : " + text;
            StableStorage storage = StableStorage.getInstance();
            VectorClock updated = storage.getCurrentVectorClock(RoomStateManager.getInstance().getRoomName())
                    .increment(storage.getIndex(RoomStateManager.getInstance().getRoomName(), RoomStateManager.getInstance().getUsername()));
            Message msgToSend = new Message(text, updated);
            ChatMessage chatMessage = new ChatMessage(msgToSend, RoomStateManager.getInstance().getUsername(), RoomStateManager.getInstance().getRoomName());
            storage.deliverMessage(RoomStateManager.getInstance().getRoomName(), msgToSend);
            String myEndpoint = RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort();

            if (RoomStateManager.getInstance().getConnected()) {
                storage.getParticipants(RoomStateManager.getInstance().getRoomName()).stream()
                        .filter(p -> !p.name().equals(RoomStateManager.getInstance().getUsername()))
                        .forEach(p -> {
                            if (!p.ipAddress().equals("null")) {
                                chatMessage.sendMessage(p);
                            } else {
                                new GetUserAddressMessage(p, myEndpoint, RoomStateManager.getInstance().getRoomName(), false, chatMessage)
                                        .sendMessage(new Participant(0, "-", ReplicationManager.getInstance()
                                                .getRoomNodes().get(p.name().charAt(0) - 'a'))
                                        );
                            }
                        });
            } else {
                storage.storeUnsentMessage(RoomStateManager.getInstance().getRoomName(), msgToSend);
            }
        } else System.out.println("Invalid command");
    }
}
