package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.HomeState;
import it.polimi.States.RoomStateManager;

public class NewRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()){}
        // TODO: create room with users and notify them
    }
}
