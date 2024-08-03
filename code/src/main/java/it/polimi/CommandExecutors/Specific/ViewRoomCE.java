package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.HomeState;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;

import java.util.Scanner;

public class ViewRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which room you want to enter?");
        String roomName = scanner.nextLine();

        RoomStateManager.getInstance().setRoomName(roomName);
        RoomStateManager.getInstance().setCurrentState(InRoomState.getInstance());
    }}
}
