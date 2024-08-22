package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.States.HomeState;
import it.polimi.States.InRoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.StableStorage;

import java.util.List;

public class ListRoomsCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()){
            StableStorage storage = StableStorage.getInstance();
            List<String> rooms = storage.getRoomNames();
            System.out.println("Rooms available: ");
            for(String room : rooms){
                System.out.println(room);
            }
        }
    }
}
