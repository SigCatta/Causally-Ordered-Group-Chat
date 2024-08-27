package it.polimi.Message;

import it.polimi.Entities.Participant;
import it.polimi.States.RoomState;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public abstract class Message implements Serializable {
    protected String content;

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public abstract void process(RoomState state);

    public void sendMessage(Participant participant) {
        String[] parts = participant.ipAddress().split(":");
        int port = Integer.parseInt(parts[1]);
        try (Socket socket = new Socket(parts[0], port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(this);
            out.flush();
        } catch (Exception e) {
            handleException();
        }
    }

    public void handleException() {
    }
}
