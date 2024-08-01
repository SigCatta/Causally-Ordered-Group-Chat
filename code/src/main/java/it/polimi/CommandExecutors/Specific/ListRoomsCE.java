package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.HomeState;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;

public class ListRoomsCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()){}
        // TODO: show the list of the rooms of this specific user
    }
}
