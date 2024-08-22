package it.polimi.Message;

import it.polimi.Entities.Participant;
import it.polimi.States.RoomState;

import java.io.IOException;
import java.io.ObjectInputStream;
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
        } catch (IOException e) {
            handleException();
            System.err.println("Failed to send message to " + participant.name() + " at " + participant.ipAddress());
            //TODO : consider the fact that this specific user must be notified when reconnecting
            e.printStackTrace();
        }
    }

    public String sendMessageAndGetResponse(String address){
        String[] parts = address.split(":");
        int port = Integer.parseInt(parts[1]);
        String response = null;

        try (Socket socket = new Socket(parts[0], port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(this);
            out.flush();

            Object obj = in.readObject();

            if (obj instanceof String) {
                response = (String) obj;
            } else {
                System.err.println("Unexpected response type received.");
            }

        } catch (IOException | ClassNotFoundException e) {
            handleException();
            e.printStackTrace();
        }

        return response;
    }

    public void handleException(){
    }
}
