package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Main;
import it.polimi.States.RoomStateManager;

public class ConnectCE implements CommandExecutor {
    @Override
    public void execute() {
        new Thread(() -> Main.startListening(RoomStateManager.getInstance().getIp(), RoomStateManager.getInstance().getPort(), RoomStateManager.getInstance().getUsername())).start();
       // Main.startup(RoomStateManager.getInstance().getIp()+":"+ RoomStateManager.getInstance().getPort());
        RoomStateManager.getInstance().setConnected(true);
    }
}
