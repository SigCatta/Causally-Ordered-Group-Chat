package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Message.HelloMessage;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SendMessageCE implements CommandExecutor {
    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Insert IP address: ");
        String ip = scanner.nextLine();
        System.out.print("Insert a valid port: ");
        int port = scanner.nextInt();
        HelloMessage message = new HelloMessage("Hello, this is a test message");
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
            out.reset();
            System.out.println("Message sent");
        } catch (Exception e) {
            System.out.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}