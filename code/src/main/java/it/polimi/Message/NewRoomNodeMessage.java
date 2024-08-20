package it.polimi.Message;

import it.polimi.Entities.Participant;
import it.polimi.States.RoomState;

import java.io.Serializable;
import java.util.List;

public class NewRoomNodeMessage extends Message implements Serializable {
    String roomName;
    List<Participant> participants;
    public NewRoomNodeMessage(String roomName, List<Participant> participants) {
        super("You have a new room to add!");
        this.roomName = roomName;
        this.participants = participants;
    }
    public String getRoomName(){
        return roomName;
    }

    public List<Participant> getParticipants() {
        return participants;
    }
    @Override
    public void process(RoomState state) {
        state.handle(this);
    }
}
