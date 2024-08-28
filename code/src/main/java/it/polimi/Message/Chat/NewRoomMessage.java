package it.polimi.Message.Chat;

import it.polimi.Entities.Participant;
import it.polimi.Message.Message;
import it.polimi.States.RoomState;

import java.io.Serializable;
import java.util.List;

public class NewRoomMessage extends Message implements Serializable {
    String roomName;
    List<Participant> participants;
    public NewRoomMessage(String roomName, List<Participant> participants) {
        super("You are part of a new room!");
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

    @Override
    public void handleException(Participant participant){
    }

}
