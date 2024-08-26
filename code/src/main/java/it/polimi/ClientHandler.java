package it.polimi;


import it.polimi.Message.Message;
import it.polimi.States.RoomStateManager;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private volatile Socket clientSocket;
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
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            handleClientConnection();
        } catch (Exception e) {
            disconnect();
            RoomStateManager.getInstance().setConnected(false);
            new Thread(this::reconnectInBackground).start(); // Start reconnection in a new thread
        }
    }

    @SuppressWarnings("BusyWait")
    private void reconnectInBackground() {
        while (true) {
            try {
                clientSocket = new Socket(ip, port);
                setupStreams();
                System.out.println("Reconnected to server");
                state.setConnected(true);
                Main.startup(ip + ":" + port);
                run(); // Restart handling messages once reconnected
                break;
            } catch (IOException e) {
                System.out.println("Reconnection failed. Retrying in 30 seconds...");
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
        } catch (IOException e) {
            System.out.println("Failed to close client connection: " + e.getMessage());
        }
    }

    private void handleClientConnection() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = (Message) input.readObject();
                message.process(state.getCurrentState());
            }
        } catch (Exception e) {
            if (e instanceof EOFException) disconnect();
            else {
                e.printStackTrace();
                disconnect();
            }
        }
    }
}
