package it.polimi.Message;

import it.polimi.States.RoomState;

import java.io.Serializable;

public class HelloMessage extends Message implements Serializable {

    public HelloMessage(String content) {
        super(content);
    }

    @Override
    public void process(RoomState state) {
        state.handle(this);
    }
}
