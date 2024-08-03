package it.polimi.States;

import it.polimi.Message.HelloMessage;
import it.polimi.Message.NewRoomMessage;

public interface RoomState{
    void handle(HelloMessage helloMessage);
    void handle(NewRoomMessage message);
}
