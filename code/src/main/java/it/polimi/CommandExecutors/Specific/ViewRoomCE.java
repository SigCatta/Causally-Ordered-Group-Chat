package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.HomeState;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.StableStorage;

import java.util.Scanner;

public class ViewRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which room you want to enter?");
        String roomName = scanner.nextLine();
        StableStorage storage = StableStorage.getInstance();
        if(storage.doesRoomExist(roomName)){
        RoomStateManager.getInstance().setRoomName(roomName);
        RoomStateManager.getInstance().setCurrentState(InRoomState.getInstance());
        storage.getChat(roomName);
        }
        else System.out.println("Room does not exist");
    }}
}
