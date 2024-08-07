package it.polimi.CommandExecutors;

import it.polimi.CommandExecutors.Specific.*;

public class CommandExecutorFactory {
    public static CommandExecutor getCommand(String command) {
        switch (command) {
            case "send-message":
                return new SendMessageCE();
            case "create-room":
                return new NewRoomCE();
            case "enter-room":
                return new ViewRoomCE();
            case "delete-room":
                return new DeleteRoomCE();
            case "list-rooms":
                return new ListRoomsCE();
            case "exit-room":
                return new ExitRoomCE();
        }
        return new InvalidCE(command);
    }
}
