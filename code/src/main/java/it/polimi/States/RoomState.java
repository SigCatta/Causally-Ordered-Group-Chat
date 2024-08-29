package it.polimi.States;

import it.polimi.Message.*;
import it.polimi.Message.Chat.ChatMessage;
import it.polimi.Message.Chat.DeleteMessage;
import it.polimi.Message.Chat.NewRoomMessage;
import it.polimi.Message.Chat.UpdateChatRequestMessage;
import it.polimi.Message.RoomNodes.DeleteNodeMessage;
import it.polimi.Message.RoomNodes.NewRoomNodeMessage;

public interface RoomState{
    void handle(HelloMessage helloMessage);
    void handle(NewRoomMessage message);
    void handle(DeleteMessage message);
    void handle(ChatMessage message);
    void handle(NewRoomNodeMessage message);
    void handle(DeleteNodeMessage message);
}
