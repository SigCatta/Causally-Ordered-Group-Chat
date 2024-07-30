package it.polimi;

import it.polimi.Controller.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Insert IP address: ");
        String ip = scanner.nextLine();

        System.out.print("Insert a valid port: ");
        int port = scanner.nextInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket client = serverSocket.accept();

                    client.setSoTimeout(10000);

                    ClientHandler clientHandler = new ClientHandler(client);
                    Thread thread = new Thread(clientHandler, "ss_handler" + client.getInetAddress());
                    thread.start();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }}
}