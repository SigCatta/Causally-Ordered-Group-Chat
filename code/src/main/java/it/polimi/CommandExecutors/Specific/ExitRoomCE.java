package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.HomeState;
import it.polimi.States.RoomStateManager;

public class ExitRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        RoomStateManager.getInstance().setCurrentState(HomeState.getInstance());
    }
}
