package it.polimi.States;

public class RoomStateManager {
    private static RoomStateManager instance;
    private RoomState currentState;

    private RoomStateManager() {
        currentState = HomeState.getInstance();
    }

    public static RoomStateManager getInstance() {
        if (instance == null) {
            instance = new RoomStateManager();
        }
        return instance;
    }

    public RoomState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(RoomState newState) {
        this.currentState = newState;
        System.out.println("State changed to "+newState.getClass().getSimpleName());
    }
}
