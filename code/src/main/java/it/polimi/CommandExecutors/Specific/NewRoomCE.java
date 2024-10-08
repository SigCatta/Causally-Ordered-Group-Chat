package it.polimi.CommandExecutors.Specific;

import it.polimi.CommandExecutors.CommandExecutor;
import it.polimi.Entities.Participant;
import it.polimi.Message.Chat.NewRoomMessage;
import it.polimi.Message.RoomNodes.NewRoomNodeMessage;
import it.polimi.Message.UserNodes.GetUserAddressMessage;
import it.polimi.States.HomeState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;
import it.polimi.Storage.StableStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NewRoomCE implements CommandExecutor {
    @Override
    public void execute() {
        if (RoomStateManager.getInstance().getCurrentState() == HomeState.getInstance()) {
            Scanner scanner = new Scanner(System.in);
            String myEndpoint = RoomStateManager.getInstance().getMyEndpoint();

            System.out.println("Insert the name of the room: ");
            String roomName = scanner.nextLine();
            System.out.println("Insert number of users to add (except you): ");
            int numberOfUsers = scanner.nextInt();

            // Get the list of usernames
            List<String> users = new ArrayList<>();
            for (int i = 0; i < numberOfUsers; i++) {
                System.out.println("Insert the name of the user: ");
                users.add(i, scanner.next());
            }
            users.add(users.size(), RoomStateManager.getInstance().getUsername());

            // Tell the node responsible for that room to create it
            new NewRoomNodeMessage(roomName, users)
                    .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getRoomNodes().get(roomName.charAt(0) - 'a')));

            // Add the room to the map in case the responsible node cannot be reached ~ it will be added eventually when the node is reachable, or I become the responsible node
            if (!RoomStateManager.getInstance().getConnected()) {
                ReplicationManager.getInstance().addRoom(roomName, users);
            }

            // Create a list of all participants (with null ip and port)
            List<Participant> participants = new ArrayList<>();
            users.stream()
                    .map(username -> new Participant(participants.size(), username, null))
                    .forEach(participants::add);
            participants.set(numberOfUsers, new Participant(numberOfUsers, RoomStateManager.getInstance().getUsername(), myEndpoint));

            // Create the room locally
            StableStorage.getInstance().initNewRoom(roomName, participants);

            // Ask for the ip of each user
            NewRoomMessage message = new NewRoomMessage(roomName, participants);
            participants.stream()
                    .filter(participant -> !participant.name().equals(RoomStateManager.getInstance().getUsername()))
                    .forEach(p -> new GetUserAddressMessage(p, myEndpoint, roomName, message)
                            .sendMessage(new Participant(0, "-", ReplicationManager.getInstance().getUserNodes().get(p.name().charAt(0) - 'a')))
                    );

        }

    }
}
