package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;

public class InvalidCE implements CommandExecutor {
    @Override
    public void execute() {
        System.out.println("Invalid command");
    }
}
