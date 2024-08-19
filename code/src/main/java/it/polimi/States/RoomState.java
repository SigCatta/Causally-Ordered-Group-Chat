package it.polimi.States;

import it.polimi.Message.*;

public interface RoomState{
    void handle(HelloMessage helloMessage);
    void handle(NewRoomMessage message);
    void handle(DeleteMessage message);
    void handle(ChatMessage message);
    void handle(UpdateChatRequestMessage message);
}
