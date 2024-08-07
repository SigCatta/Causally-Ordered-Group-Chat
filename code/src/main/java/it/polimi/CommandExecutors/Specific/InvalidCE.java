package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Message;
import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.ChatMessage;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.StableStorage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class InvalidCE implements CommandExecutor {
    private String command;

    public InvalidCE(String command) {
        this.command = command;
    }
    private void sendMessage(Participant participant, ChatMessage message, Message m) {
        String[] parts = participant.ipAddress().split(":");
        int port = Integer.parseInt(parts[1]);
        try (Socket socket = new Socket(parts[0], port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            //TODO : not in unsent but in undelivered or something similar
            StableStorage storage = new StableStorage();
            storage.storeUnsentMessage(RoomStateManager.getInstance().getRoomName(),m);
        }
    }
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == InRoomState.getInstance()){
            String text = command;
            text = RoomStateManager.getInstance().getUsername() + " : " + text;
            StableStorage storage = new StableStorage();
            VectorClock vectorClock = storage.getCurrentVectorClock(RoomStateManager.getInstance().getRoomName());
            VectorClock updated=vectorClock.increment(storage.getIndex(RoomStateManager.getInstance().getRoomName(),RoomStateManager.getInstance().getUsername()));
            Message m = new Message(text,updated);
            ChatMessage chatMessage = new ChatMessage(m, RoomStateManager.getInstance().getUsername(), RoomStateManager.getInstance().getRoomName());
            storage.deliverMessage(RoomStateManager.getInstance().getRoomName(),m);
            if(RoomStateManager.getInstance().getConnected()) {
                List<Participant> participants = storage.getParticipants(RoomStateManager.getInstance().getRoomName());
                for (Participant participant : participants) {
                    if(!participant.name().equals(RoomStateManager.getInstance().getUsername())){
                        sendMessage(participant, chatMessage,m);}
                }
            }else{
                storage.storeUnsentMessage(RoomStateManager.getInstance().getRoomName(),m);
            }}
        else System.out.println("Invalid command");
    }
}
