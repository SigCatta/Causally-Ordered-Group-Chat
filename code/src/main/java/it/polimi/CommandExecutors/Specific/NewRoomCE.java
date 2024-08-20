package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Participant;
import it.polimi.Message.NewRoomMessage;
import it.polimi.Message.NewRoomNodeMessage;
import it.polimi.States.HomeState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if(RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Insert the name of the room: ");
            String roomName = scanner.nextLine();
            int x = roomName.charAt(0)-97;
            System.out.println("Insert number of users to add (except you): ");
            Integer numberOfUsers = scanner.nextInt();
            List<Participant> participants = new ArrayList<>();
            for(int i = 0; i < numberOfUsers; i++){
                System.out.println("Insert the name of the user: ");
                String userName = scanner.next();
                System.out.println("Insert the IP address of the user: ");
                String userIP = scanner.next();
                System.out.println("Insert the port of the user: ");
                Integer userPort = scanner.nextInt();
                scanner.nextLine();
                userIP = userIP+":"+userPort;
                participants.add(new Participant(i, userName, userIP));
            }
            participants.add(new Participant(numberOfUsers, RoomStateManager.getInstance().getUsername(), RoomStateManager.getInstance().getIp()+":"+RoomStateManager.getInstance().getPort()));
            StableStorage s = new StableStorage();
            s.initNewRoom(roomName, participants);

           NewRoomMessage message = new NewRoomMessage(roomName, participants);
           ExecutorService executor = Executors.newFixedThreadPool(10);
            for (Participant participant : participants) {
                executor.submit(() -> {
              if(!participant.name().equals(RoomStateManager.getInstance().getUsername())){
                message.sendMessage(participant);
            }});}
            executor.close();
            String[] roomNodes = ReplicationManager.getInstance().getRoomNodes();
            String address = roomNodes[x];
            NewRoomNodeMessage m = new NewRoomNodeMessage(roomName,participants);
            m.sendMessage(new Participant(0,"x",address));
        }

    }
}
