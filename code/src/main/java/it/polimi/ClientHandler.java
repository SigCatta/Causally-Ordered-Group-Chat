package it.polimi;

import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.Message.Message;
import it.polimi.Message.UpdateChatRequestMessage;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.StableStorage;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            e.printStackTrace();
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

    private void reconnectInBackground() {
        while (true) {
            try {
                clientSocket = new Socket(ip, port);
                setupStreams();
                System.out.println("Reconnected to server");
                state.setConnected(true);
                update_chats();
                run(); // Restart handling messages once reconnected
                break;
            } catch (IOException e) {
                System.out.println("Reconnection failed. Retrying in 30 seconds...");
                try {
                    Thread.sleep(30000); // Wait before retrying
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                }
            }
        }
    }

    public void update_chats() {
        StableStorage storage = StableStorage.getInstance();
        List<String> rooms = storage.getRoomNames();
        for (String room : rooms) {
            List<Participant> participants = storage.getParticipants(room);
            VectorClock vc = storage.getCurrentVectorClock(room);
            List<it.polimi.Entities.Message> unsentMessages = storage.getUnsentMessages(room);
            UpdateChatRequestMessage message = new UpdateChatRequestMessage(room, state.getUsername(), vc, unsentMessages);
            ExecutorService executor = Executors.newFixedThreadPool(10);
            for (Participant participant : participants) {
                executor.submit(() -> {
                    if (!participant.name().equals(RoomStateManager.getInstance().getUsername())) {
                        message.sendMessage(participant);
                    }
                });
            }
            executor.close();
        }
    }


    private void disconnect() {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
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
