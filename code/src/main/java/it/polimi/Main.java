package it.polimi;

import it.polimi.CommandExecutors.CommandExecutorFactory;

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

        // Start the server thread
        new Thread(() -> startListening(ip, port, username)).start();

       readLine();
    }

    public static void startListening(String ip, int port, String username) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket client = serverSocket.accept();

                    client.setSoTimeout(10000);

                    ClientHandler clientHandler = new ClientHandler(client, ip, port, username);
                    Thread thread = new Thread(clientHandler, "ss_handler" + client.getInetAddress());
                    thread.start();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
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
}