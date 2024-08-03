package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Participant;
import it.polimi.Message.NewRoomMessage;
import it.polimi.States.HomeState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.StableStorage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NewRoomCE implements CommandExecutor {
    private void sendRoomCreationMessage(Participant participant, NewRoomMessage message, Integer port) {
        try (Socket socket = new Socket(participant.ipAddress(), port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
            out.reset();
        } catch (IOException e) {
            System.err.println("Failed to send message to " + participant.name() + " at " + participant.ipAddress());
            e.printStackTrace();
        }
    }
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Insert the name of the room: ");
            String roomName = scanner.nextLine();
            System.out.println("Insert number of users for this room: ");
            Integer numberOfUsers = scanner.nextInt();
            List<Participant> participants = new ArrayList<>();
            List<Integer> ports = new ArrayList<>();
            for(int i = 0; i < numberOfUsers; i++){
                System.out.println("Insert the name of the user: ");
                String userName = scanner.next();
                System.out.println("Insert the IP address of the user: ");
                String userIP = scanner.next();
                System.out.println("Insert the port of the user: ");
                Integer userPort = scanner.nextInt();
                scanner.nextLine();
                ports.add(userPort);
                System.out.println("dati"+userName+userIP+userPort);
                participants.add(new Participant(i, userName, userIP));
            }
            StableStorage ss = new StableStorage();
            ss.initNewRoom(roomName, participants);

            participants.add(new Participant(numberOfUsers, RoomStateManager.getInstance().getUsername(), RoomStateManager.getInstance().getIp()));
            NewRoomMessage message = new NewRoomMessage(roomName, participants);
            for (Participant participant : participants) {
                sendRoomCreationMessage(participant, message, ports.get(participant.index()));
            }
        }

    }
}
