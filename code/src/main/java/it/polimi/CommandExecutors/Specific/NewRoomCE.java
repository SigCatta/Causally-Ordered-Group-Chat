package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Participant;
import it.polimi.Message.NewRoomMessage;
import it.polimi.Message.NewRoomNodeMessage;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.States.HomeState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class NewRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if (RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Insert the name of the room: ");
            String roomName = scanner.nextLine();
            int x = roomName.charAt(0) - 97;
            System.out.println("Insert number of users to add (except you): ");
            Integer numberOfUsers = scanner.nextInt();
            List<String> participants = new ArrayList<>();
            for (int i = 0; i < numberOfUsers; i++) {
                System.out.println("Insert the name of the user: ");
                String userName = scanner.next();
                participants.add(numberOfUsers,userName);
            }
            List<String> roomNodes = ReplicationManager.getInstance().getRoomNodes();
            AtomicReference<String> address = new AtomicReference<>(roomNodes.get(x));
            NewRoomNodeMessage m = new NewRoomNodeMessage(roomName, participants);
            m.sendMessage(new Participant(0, "x", address.get()));
            List<Participant> p = new ArrayList<>();
            String a = RoomStateManager.getInstance().getIp()+":"+RoomStateManager.getInstance().getPort();
            participants.forEach( username -> {
                GetUserAddressMessage message = new GetUserAddressMessage(username,a);
                List<String> userNodes = ReplicationManager.getInstance().getUserNodes();
                String ind = userNodes.get(username.charAt(0));
                try {
                    String participantAddress = message.sendMessageAndGetResponse(ind);
                    Participant participant = new Participant(p.size() , username, participantAddress);
                    p.add(participant);
                } catch (Exception e) {
                    Participant participant = new Participant(p.size() , username, null);
                    p.add(participant);
                }
            });
            p.add(new Participant(numberOfUsers, RoomStateManager.getInstance().getUsername(), RoomStateManager.getInstance().getIp() + ":" + RoomStateManager.getInstance().getPort()));
            StableStorage s = StableStorage.getInstance();
            s.initNewRoom(roomName, p);

            NewRoomMessage message = new NewRoomMessage(roomName, p);
            ExecutorService executor = Executors.newFixedThreadPool(10);
            for (Participant participant : p) {
                if(participant.ipAddress()!= null){
                executor.submit(() -> {
                    if (!participant.name().equals(RoomStateManager.getInstance().getUsername())) {
                        message.sendMessage(participant);
                    }
                });
            }}
            executor.close();
        }

    }
}
