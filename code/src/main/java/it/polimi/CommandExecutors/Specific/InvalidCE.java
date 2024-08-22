package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Message;
import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.ChatMessage;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.util.List;

public class InvalidCE implements CommandExecutor {
    private String command;

    public InvalidCE(String command) {
        this.command = command;
    }

    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == InRoomState.getInstance()){
            String text = command;
            text = RoomStateManager.getInstance().getUsername() + " : " + text;
            StableStorage storage = StableStorage.getInstance();
            VectorClock vectorClock = storage.getCurrentVectorClock(RoomStateManager.getInstance().getRoomName());
            VectorClock updated=vectorClock.increment(storage.getIndex(RoomStateManager.getInstance().getRoomName(),RoomStateManager.getInstance().getUsername()));
            Message m = new Message(text,updated);
            ChatMessage chatMessage = new ChatMessage(m, RoomStateManager.getInstance().getUsername(), RoomStateManager.getInstance().getRoomName());
            storage.deliverMessage(RoomStateManager.getInstance().getRoomName(),m);
            String a = RoomStateManager.getInstance().getIp()+":"+RoomStateManager.getInstance().getPort();
            if(RoomStateManager.getInstance().getConnected()) {
                List<Participant> participants = storage.getParticipants(RoomStateManager.getInstance().getRoomName());
                for (Participant participant : participants) {
                    if(!participant.name().equals(RoomStateManager.getInstance().getUsername())){
                        if(participant.ipAddress()!=null){
                            chatMessage.sendMessage(participant);
                        }
                        else{
                            GetUserAddressMessage message = new GetUserAddressMessage(participant.name(),a);
                            List<String> userNodes = ReplicationManager.getInstance().getUserNodes();
                            String ind = userNodes.get(participant.name().charAt(0));
                            try {
                                String participantAddress = message.sendMessageAndGetResponse(ind);
                                StableStorage.getInstance().updateParticipantIp(RoomStateManager.getInstance().getRoomName(), new Participant(participant.index(),participant.name(),participantAddress));
                                chatMessage.sendMessage(participant);
                            } catch (Exception e) {

                            }
                        }
                    }
                }
            }else{
                storage.storeUnsentMessage(RoomStateManager.getInstance().getRoomName(),m);
            }}
        else System.out.println("Invalid command");
    }
}
