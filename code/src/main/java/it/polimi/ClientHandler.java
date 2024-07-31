package it.polimi;

import it.polimi.Message.Message;
import it.polimi.States.HomeState;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final String ip;
    private final int port;
    private RoomStateManager state;

    public ClientHandler(Socket clientSocket, String ip, int port) {
        this.clientSocket = clientSocket;
        this.ip = ip;
        this.port = port;
        this.state= RoomStateManager.getInstance();
        try {
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting handling message");
            handleClientConnection();
        } catch (Exception e) {
            disconnect();
        }
    }

    private void disconnect() {
        try {
            if (input != null) {
                input.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Failed to close client connection: " + e.getMessage());
        }
        Thread.currentThread().interrupt();
    }

    private void handleClientConnection() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = (Message) input.readObject();
                message.process(state.getCurrentState());
            }
        } catch (ClassCastException | ClassNotFoundException | IOException e) {
            disconnect();
        }
    }
}
