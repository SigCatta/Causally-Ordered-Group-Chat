package it.polimi.States;

public class RoomStateManager {
    private static RoomStateManager instance;
    private RoomState currentState;
    private String roomName;
    private Boolean isConnected;
    private String ip;
    private int port;
    private String username;

    private RoomStateManager() {
        isConnected = true;
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
        System.out.println("State changed to " + newState.getClass().getSimpleName());
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


    public Boolean getConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMyEndpoint() {
        return ip + ":" + port;
    }
}
