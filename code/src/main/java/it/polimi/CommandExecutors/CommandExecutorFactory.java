package it.polimi.CommandExecutors;

import it.polimi.CommandExecutors.Specific.InvalidCE;
import it.polimi.CommandExecutors.Specific.SendMessageCE;

public class CommandExecutorFactory {
    public static CommandExecutor getCommand(String command) {
        switch (command) {
            case "sendMessage":
                return new SendMessageCE();

        }
        return new InvalidCE();
    }
}
