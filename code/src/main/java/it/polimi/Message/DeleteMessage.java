package it.polimi.Message;

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

}
