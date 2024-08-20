package it.polimi.Message;

import it.polimi.States.RoomState;

import java.io.Serializable;

public class DeleteMessageNode extends Message implements Serializable {
    private final String roomName;

    public DeleteMessageNode (String roomName){
        super("You have a room to delete!");
        this.roomName = roomName;
    }

    @Override
    public void process(RoomState state) {
        state.handle(this);
    }

    public String getRoomName() {
        return roomName;
    }
}
