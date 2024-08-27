package it.polimi.Message.RoomNodes;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;

import java.io.Serializable;

public class DeleteNodeMessage extends Message implements Serializable {
    private final String roomName;

    public DeleteNodeMessage(String roomName){
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
