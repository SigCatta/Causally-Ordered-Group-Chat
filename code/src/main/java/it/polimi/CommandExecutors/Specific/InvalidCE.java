package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;

public class InvalidCE implements CommandExecutor {
    @Override
    public void execute() {
        System.out.println("Invalid command");
    }
}
