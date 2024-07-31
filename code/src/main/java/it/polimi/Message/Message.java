package it.polimi.Message;

import it.polimi.States.RoomState;

import java.io.Serializable;

public abstract class Message implements Serializable {
    protected String content;

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public abstract void process(RoomState state);
}
