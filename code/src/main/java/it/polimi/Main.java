package it.polimi;

import it.polimi.CommandExecutors.CommandExecutorFactory;
import it.polimi.Entities.Participant;
import it.polimi.Message.Replication.HelpMessage;
import it.polimi.Message.Replication.RingDataRequestMessage;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert username:");
        String username = scanner.next();

        System.out.print("Insert IP address: ");
        String ip = scanner.next();

        System.out.print("Insert a valid port: ");
        int port = scanner.nextInt();

        RoomStateManager state = RoomStateManager.getInstance();
        state.setIp(ip);
        state.setPort(port);
        state.setUsername(username);

        System.out.println("Do you want to create a new network? (y/n)");
        String choice = scanner.next();
        if (choice.equals("n")) {
            for (int i = 0; i < 26; i++) {
                ReplicationManager.getInstance().getRoomNodes().add(null);
                ReplicationManager.getInstance().getUserNodes().add(null);
            }
            System.out.println("Insert IP of a member:");
            String memberIp = scanner.next();

            System.out.println("Insert port of a member:");
            int memberPort = scanner.nextInt();

            startup(memberIp + ':' + memberPort);
        } else if (!choice.equals("y")) {
            throw new RuntimeException("Invalid choice");
        } else {
            // the node who creates the network is responsible for everything in both rings
            String entry = ip + ':' + port;
            for (int i = 0; i < 26; i++) {
                ReplicationManager.getInstance().getRoomNodes().add(entry);
                ReplicationManager.getInstance().getUserNodes().add(entry);
            }
            System.out.println("Network created!");
        }

        // Start the server thread
        new Thread(() -> startListening(ip, port, username)).start();

        readLine();
    }

    public static void startListening(String ip, int port, String username) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            RoomStateManager.getInstance().setConnected(true);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket client = serverSocket.accept();
                    client.setSoTimeout(30000);
                    ClientHandler clientHandler = new ClientHandler(client, ip, port, username);
                    Thread thread = new Thread(clientHandler, "ss_handler" + client.getInetAddress());
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            RoomStateManager.getInstance().setConnected(false);
        }
    }

    public static void readLine() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter commands (type 'exit' to quit):");

        while (true) {
            String command = scanner.nextLine();

            if ("exit".equals(command)) {
                System.out.println("Exiting...");
                break;
            } else {
                CommandExecutorFactory.getCommand(command).execute();
            }
        }

        scanner.close();
    }

    @SuppressWarnings("BusyWait")
    private static void startup(String endpoint){
        new RingDataRequestMessage(RoomStateManager.getInstance().getIp() + ':' + RoomStateManager.getInstance().getPort())
                .sendMessage(new Participant(0, "-", endpoint));

        int i = 0;
        while (ReplicationManager.getInstance().getRoomNodes().contains(null) || ReplicationManager.getInstance().getUserNodes().contains(null)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(i++ > 10){
                System.out.println("Failed to connect to the network. Exiting...");
                System.exit(1);
            }
        }

        // TODO: if any of my rooms have been deleted, delete them from the storage
        // TODO: maybe nodes should only have the deleted rooms that were under their responsibility

        // Help the nodes that are currently responsible for the most data
        new HelpMessage(true, false)
                .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().chooseRoomNodeToHelp()));
        new HelpMessage(false, true)
                .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().chooseUserNodeToHelp()));

    }
}