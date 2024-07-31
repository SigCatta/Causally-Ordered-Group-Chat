package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;

public class ViewRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        RoomStateManager.getInstance().setCurrentState(InRoomState.getInstance());
    }
}
