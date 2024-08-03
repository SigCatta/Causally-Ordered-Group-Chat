package it.polimi;

import it.polimi.Message.Message;
import it.polimi.States.RoomStateManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private volatile Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final String ip;
    private final int port;
    private RoomStateManager state;

    public ClientHandler(Socket clientSocket, String ip, int port, String username) {
        this.clientSocket = clientSocket;
        this.ip = ip;
        this.port = port;
        this.state = RoomStateManager.getInstance();
        RoomStateManager.getInstance().setUsername(username);
        RoomStateManager.getInstance().setIp(ip);
        RoomStateManager.getInstance().setPort(port);
        this.state.setConnected(true);
        setupStreams();
    }

    private void setupStreams() {
        try {
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error setting up streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting handling message");
            handleClientConnection();
        } catch (Exception e) {
            System.out.println("Connection lost. Attempting to reconnect...");
          //  new Thread(this::reconnectInBackground).start(); // Start reconnection in a new thread
        }
    }

    private void reconnectInBackground() {
        while (true) {
            try {
                clientSocket = new Socket(ip, port);
                setupStreams();
                System.out.println("Reconnected to server");
                state.setConnected(true);
                run(); // Restart handling messages once reconnected
                break;
            } catch (IOException e) {
                System.out.println("Reconnection failed. Retrying in 5 seconds...");
                try {
                    Thread.sleep(5000); // Wait before retrying
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                }
            }
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
            RoomStateManager.getInstance().setConnected(false);
        } catch (IOException e) {
            System.out.println("Failed to close client connection: " + e.getMessage());
        }
    }

    private void handleClientConnection() throws IOException, ClassNotFoundException {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = (Message) input.readObject();
                message.process(state.getCurrentState());
            } catch (ClassCastException | ClassNotFoundException | IOException e) {
                System.out.println("Error in handling client connection: " + e.getMessage());
                disconnect();
                throw e; // Rethrow to trigger reconnection
            }
        }
    }
}
