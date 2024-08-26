package it.polimi.CommandExecutors.Specific;

import it.polimi.ClientHandler;
import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Main;
import it.polimi.States.RoomStateManager;

import java.util.Scanner;

public class ConnectCE implements CommandExecutor {
    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert IP of a member:");
        String memberIp = scanner.next();
        System.out.println("Insert port of a member:");
        int memberPort = scanner.nextInt();
        new Thread(() -> Main.startListening(RoomStateManager.getInstance().getIp(), RoomStateManager.getInstance().getPort(), RoomStateManager.getInstance().getUsername())).start();
        new Thread(() -> Main.startup(memberIp+":"+ memberPort)).start();
        RoomStateManager.getInstance().setConnected(true);
    }
}
