package it.polimi.Message.Chat;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;

import java.io.Serializable;


public class ChatMessage extends Message implements Serializable {
    private final it.polimi.Entities.Message message;
    private String sender;

    private String roomName;

    public ChatMessage(it.polimi.Entities.Message message, String sender, String roomName) {
        super("new message in chat");
        this.message= message;
        this.sender = sender;
        this.roomName = roomName;
    }

    public it.polimi.Entities.Message getMessage() {
        return message;
    }


    @Override
    public void process(RoomState state) {
            state.handle(this);
    }

    @Override
    public void handleException(Participant participant){

    }


    public String getRoomName() {
        return roomName;
    }

    public String getSender() {
        return sender;
    }
}
