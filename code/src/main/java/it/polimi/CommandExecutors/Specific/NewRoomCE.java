package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.HomeState;
import it.polimi.States.RoomStateManager;

import java.util.Scanner;

public class NewRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Insert the name of the room: ");
            String roomName = scanner.nextLine();
            System.out.println("Insert the name of the users you want to invite (separated by commas): ");
            String users = scanner.nextLine();

        }
        // TODO: create room with users and notify them
    }
}
