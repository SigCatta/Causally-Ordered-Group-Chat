package it.polimi;

import it.polimi.CommandExecutors.CommandExecutorFactory;
import it.polimi.Entities.DataContainer;
import it.polimi.Entities.Participant;
import it.polimi.Message.Chat.UpdateChatRequestMessage;
import it.polimi.Message.Replication.HelpMessage;
import it.polimi.Message.Replication.RingDataRequestMessage;
import it.polimi.Message.Replication.RoomNodeProposalMessage;
import it.polimi.Message.Replication.UserNodeProposalMessage;
import it.polimi.Message.RoomNodes.CheckForDeletionMessage;
import it.polimi.Message.RoomNodes.GetMyRoomsMessage;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.Message.UserNodes.JoinMessage;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.NodeHistoryManager;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
    private static ServerSocket serverSocket;
    private static Thread listeningThread;
    public static String endpoint;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

            endpoint = memberIp + ':' + memberPort;

            // Start the server thread
            new Thread(() -> startListening(ip, port, username)).start();

            startup();
        } else if (!choice.equals("y")) {
            throw new RuntimeException("Invalid choice");
        } else {
            if (StableStorage.getInstance().getBackupData() != null) { // if there is backup data, restore it
                DataContainer backup = StableStorage.getInstance().getBackupData();
                ReplicationManager.getInstance().getUsersMap().putAll(backup.usersMap());
                ReplicationManager.getInstance().getRoomsMap().putAll(backup.roomsMap());
                ReplicationManager.getInstance().getDeletedRooms().addAll(backup.deletedRooms());
            }

            // the node who creates the network is responsible for everything in both rings
            String entry = ip + ':' + port;
            for (int i = 0; i < 26; i++) {
                ReplicationManager.getInstance().getRoomNodes().add(entry);
                ReplicationManager.getInstance().getUserNodes().add(entry);
            }
            ReplicationManager.getInstance().addUser(state.getUsername(), state.getIp() + ':' + state.getPort());
            System.out.println("Network created!");

            // Start the server thread
            new Thread(() -> startListening(ip, port, username)).start();
        }


        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(LastWill::execute));

        scheduler.scheduleAtFixedRate(Main::startup, 0, 30, TimeUnit.SECONDS);
        new Thread(NodeHistoryManager:: resolveUserNodesPartition).start();
        new Thread(NodeHistoryManager:: resolveRoomNodesPartition).start();
        readLine();
    }

    public static void startListening(String ip, int port, String username) {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true); // Allow reuse of the port

            RoomStateManager.getInstance().setConnected(true);

            listeningThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket client = serverSocket.accept();
                        client.setSoTimeout(30000);
                        ClientHandler clientHandler = new ClientHandler(client, ip, port, username);
                        Thread thread = new Thread(clientHandler, "ss_handler" + client.getInetAddress());
                        thread.start();
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            });

            listeningThread.start();

        } catch (IOException e) {
            e.printStackTrace();
            RoomStateManager.getInstance().setConnected(false);
        }
    }

    public static void stopListening() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (listeningThread != null && listeningThread.isAlive()) {
                listeningThread.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void readLine() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter commands :");

        while (true) {
            String command = scanner.nextLine();
            CommandExecutorFactory.getCommand(command).execute();
        }
    }

    @SuppressWarnings("BusyWait")
    public static void startup() {
        RoomStateManager state = RoomStateManager.getInstance();
        String myEndpoint = state.getIp() + ':' + state.getPort();

        new RingDataRequestMessage(myEndpoint)
                .sendMessage(new Participant(0, "-", endpoint));

        int attempts = 0;
        while (ReplicationManager.getInstance().getRoomNodes().contains(null) || ReplicationManager.getInstance().getUserNodes().contains(null)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (attempts++ > 10) {
                System.out.println("Failed to connect to the network. Exiting...");
                System.exit(1);
            }
        }

        // Restore backup data if present
        sendBackups();

        // Check if any of my rooms have been deleted
        StableStorage.getInstance().getRoomNames()
                .forEach(r ->
                        new CheckForDeletionMessage(r, myEndpoint)
                                .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getRoomNodes().get(r.charAt(0) - 'a')))
                );

        // sends its ip to other node participants
        new JoinMessage(state.getUsername(), myEndpoint)
                .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getRoomNodes().get(state.getUsername().charAt(0) - 'a')));

        update_chats();

        // Check for new rooms
        ReplicationManager.getInstance().getRoomNodes().stream()
                .distinct()
                .forEach(n ->
                        new GetMyRoomsMessage(state.getUsername(), myEndpoint)
                                .sendMessage(new Participant(0, "-", n))
                );

        // Help the nodes that are currently responsible for the most data
        if(!ReplicationManager.getInstance().getRoomNodes().contains(myEndpoint)
                && !ReplicationManager.getInstance().getUserNodes().contains(myEndpoint)) {
            new HelpMessage(true, false)
                    .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().chooseRoomNodeToHelp()));
            new HelpMessage(false, true)
                    .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().chooseUserNodeToHelp()));
        }
    }

    private static void sendBackups() {
        DataContainer backup = StableStorage.getInstance().getBackupData();
        if (backup == null) return; // no backup data to restore

        IntStream.range(0, 26)
                // for each letter, send the data to the corresponding node
                .forEach(i -> {
                    if (backup.roomsMap() != null && backup.deletedRooms() != null) {
                        ConcurrentHashMap<String, List<String>> rooms = backup.roomsMap()
                                .entrySet().stream()
                                .filter(e -> e.getKey().charAt(0) - 'a' == i)
                                .collect(ConcurrentHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), ConcurrentHashMap::putAll);

                        Set<String> deletedRooms = backup.deletedRooms().stream()
                                .filter(r -> r.charAt(0) - 'a' == i)
                                .collect(HashSet::new, Set::add, Set::addAll);

                        if (!rooms.isEmpty() || !deletedRooms.isEmpty()) {
                            new RoomNodeProposalMessage(rooms, deletedRooms)
                                    .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getRoomNodes().get(i)));
                        }
                    }

                    if (backup.usersMap() != null) {
                        ConcurrentHashMap<String, String> users = backup.usersMap()
                                .entrySet().stream()
                                .filter(e -> e.getKey().charAt(0) - 'a' == i)
                                .collect(ConcurrentHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), ConcurrentHashMap::putAll);

                        if (!users.isEmpty()) {
                            new UserNodeProposalMessage(users)
                                    .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getUserNodes().get(i)));
                        }
                    }
                });

        StableStorage.getInstance().deleteBackup();
    }

    private static void update_chats() {
        StableStorage ss = StableStorage.getInstance();
        ss.getRoomNames()
                .forEach(room -> {
                    UpdateChatRequestMessage message = new UpdateChatRequestMessage(
                            room,
                            RoomStateManager.getInstance().getUsername(), // my username
                            ss.getCurrentVectorClock(room), // my current vector clock
                            ss.getUnsentMessages(room) // all unsent messages
                    );

                    String myEndpoint = RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort();
                    ss.getParticipants(room).stream()
                            .filter(participant -> !participant.name().equals(RoomStateManager.getInstance().getUsername()))
                            .forEach(p -> new GetUserAddressMessage(p, myEndpoint, room, false, message)
                                    .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getUserNodes().get(p.name().charAt(0) - 'a')))
                            );
                });
    }
}