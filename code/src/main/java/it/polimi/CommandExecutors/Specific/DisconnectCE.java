package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.LastWill;
import it.polimi.Main;
import it.polimi.States.RoomStateManager;

public class DisconnectCE implements CommandExecutor {
    @Override
    public void execute() {
        LastWill.execute();
        Main.stopListening();
        RoomStateManager.getInstance().setConnected(false);
    }
}
