package it.polimi.States;

import it.polimi.Message.HelloMessage;
import it.polimi.Message.Message;

public interface RoomState{
    void handle(HelloMessage helloMessage);
}
