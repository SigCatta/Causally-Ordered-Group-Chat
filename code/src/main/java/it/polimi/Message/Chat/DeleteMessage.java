package it.polimi.Message.Chat;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;

import java.io.Serializable;

public class DeleteMessage extends Message implements Serializable {
    private String roomName;
    public DeleteMessage(String roomName) {
        super(roomName+" deleted!");
        this.roomName = roomName;

    }
    public String getRoomName() {
        return roomName;
    }

    @Override
    public void process(RoomState state) {
        state.handle(this);
    }

    @Override
    public void handleException(Participant participant){
    }

}
