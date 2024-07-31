package it.polimi.States;

import it.polimi.Message.HelloMessage;

public interface RoomState{
    void handle(HelloMessage helloMessage);
}
