package it.polimi.CommandExecutors;

import it.polimi.CommandExecutors.Specific.*;

public class CommandExecutorFactory {
    public static CommandExecutor getCommand(String command) {
        return switch (command) {
            case "create-room" -> new NewRoomCE();
            case "enter-room" -> new ViewRoomCE();
            case "delete-room" -> new DeleteRoomCE();
            case "list-rooms" -> new ListRoomsCE();
            case "exit-room" -> new ExitRoomCE();
            default -> new InvalidCE(command);
        };
    }
}
