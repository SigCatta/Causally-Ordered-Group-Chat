package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;

public class DeleteRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == InRoomState.getInstance()){
            // TODO: send message to everyone in the room and then delete the room
        }
    }
}
