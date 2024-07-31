package it.polimi.Controller;

import it.polimi.Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Inizio a gestire messaggio");
            handleClientConnection();
        } catch (Exception e) {
            System.out.println(e);
            disconnect();
        }
    }

    private void disconnect() {
        try {
            if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e){}
    }

    private void handleClientConnection() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = (Message) input.readObject();
                message.process();
            }
        } catch (ClassCastException | ClassNotFoundException | IOException e) {
            System.out.println(e);
            disconnect();
        }
    }
}
