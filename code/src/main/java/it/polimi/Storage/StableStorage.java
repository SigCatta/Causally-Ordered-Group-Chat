package it.polimi.Storage;

import it.polimi.Entities.Message;
import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StableStorage {
    private final StorageWriter sw;

    private final StorageReader sr;

    public StableStorage() {
        this.sw = new StorageWriter();
        this.sr = new StorageReader();
    }

    // Creates and initializes all the files needed for a chat room
    public void initNewRoom(String roomId, List<Participant> participants) {
        Path roomDir = Paths.get(roomId);
        int size = participants.size();
        if (size < 2) {
            System.out.println("Go get some friends...");
            throw new IllegalArgumentException("Room size must be at least 2");
        }

        //
        // Create necessary files
        //
        sw.createFile(roomDir.resolve("messages.txt"));
        sw.createFile(roomDir.resolve("vector_clocks.txt"));
        sw.createFile(roomDir.resolve("last_vc.txt"));
        sw.createFile(roomDir.resolve("usernames.txt"));
        sw.createFile(roomDir.resolve("addresses.txt"));

        sw.createFile(roomDir.resolve(Paths.get("delayed", "messages.txt")));
        sw.createFile(roomDir.resolve(Paths.get("delayed", "vector_clocks.txt")));

        sw.createFile(roomDir.resolve("unsent_msg.txt"));
        sw.createFile(roomDir.resolve("unsent_vc.txt"));
        //
        // Initialize files
        //
        participants.forEach(p -> {
            sw.append(roomDir.resolve("usernames.txt"), p.name() + '\n');
            sw.append(roomDir.resolve("addresses.txt"), p.ipAddress() + '\n');
        });

        sw.append(roomDir.resolve("messages.txt"), "Chat room created successfully\n");

        StringBuilder sb = new StringBuilder() // Build the first vector clock: (0,0,...,0,0)
                .append("[")
                .repeat("0, ", size - 1)
                .append("0]\n");
        sw.append(roomDir.resolve("vector_clocks.txt"), sb.toString());
        sw.append(roomDir.resolve("last_vc.txt"), sb.toString());
    }

    // Returns a list of all participants in a chat room
    public List<Participant> getParticipants(String roomId) {
        return sr.getParticipants(roomId);
    }

    // Updates a participant's ip address
    public void updateParticipantIp(String roomId, Participant participant) {
        StringBuilder sb = new StringBuilder();

        // update the participant's ip address, while leaving the other ones untouched
        sr.getParticipants(roomId)
                .forEach(p -> {
                    if (p.index() == participant.index()) {
                        sb.append(participant.ipAddress()).append('\n');
                    } else {
                        sb.append(p.ipAddress()).append('\n');
                    }
                });

        sw.overwrite(Paths.get(roomId, "addresses.txt"), sb.toString());
    }

    // Returns the current vector clock
    public VectorClock getCurrentVectorClock(String roomId) {
        return sr.getCurrentVectorClock(roomId);
    }

    // DELIVERED - Saves a message to stable storage, along with the vector clock
    public void deliverMessage(String roomId, Message message) {
        VectorClock currentVC = getCurrentVectorClock(roomId);
        VectorClock newVC = message.vectorClock();

        sw.append(Paths.get(roomId, "vector_clocks.txt"), newVC.toString() + '\n');
        sw.append(Paths.get(roomId, "messages.txt"), message.text() + '\n');
        sw.overwrite(Paths.get(roomId, "last_vc.txt"), currentVC.merge(newVC).toString());
    }

    // DELAYED - saves a message to stable storage, along with the vector clock
    // messages are stored in order based on their vector clocks
    public void delayMessage(String roomId, Message message) {
        List<Message> messages = sr.getDelayedMessages(roomId);
        StringBuilder vcSB = new StringBuilder();
        StringBuilder msgSB = new StringBuilder();
        boolean ok = false;

        String newText = message.text();
        VectorClock newVC = message.vectorClock();
        // If there are no other delayed messages OR the new vc is older than all other saved, just insert the new message in front
        if (messages.isEmpty() || newVC.isOlder(messages.getFirst().vectorClock())) {
            vcSB.append(newVC).append('\n');
            msgSB.append(newText).append('\n');
            ok = true;
        }

        for (Message msg : messages) {
            // If the message is a duplicate, don't save it again
            if (msg.vectorClock().toString().equals(newVC.toString())) return;

            vcSB.append(msg.vectorClock()).append('\n');
            msgSB.append(msg.text()).append('\n');
            if (newVC.canBeDeliveredAfter(msg.vectorClock()) && !ok) {
                vcSB.append(newVC).append('\n');
                msgSB.append(newText).append('\n');
                ok = true;
            }
        }
        if (!ok) {
            vcSB.append(newVC).append('\n');
            msgSB.append(newText).append('\n');
        }

        sw.overwrite(Paths.get(roomId, "delayed", "vector_clocks.txt"), vcSB.toString());
        sw.overwrite(Paths.get(roomId, "delayed", "messages.txt"), msgSB.toString());
    }

    // Stores an unsent message to stable storage
    public void storeUnsentMessage(String roomId, Message message) {
        sw.append(Paths.get(roomId, "unsent_msg.txt"), message.text() + '\n');
        sw.append(Paths.get(roomId, "unsent_vc.txt"), message.vectorClock().toString() + '\n');
    }

    // Returns a list of all unsent messages
    public List<Message> getUnsentMessages(String roomId) {
        return sr.getUnsentMessages(roomId);
    }

    // Deletes all unsent messages
    public void deleteUnsentMessages(String roomId) {
        sw.overwrite(Paths.get(roomId, "unsent_msg.txt"), "");
        sw.overwrite(Paths.get(roomId, "unsent_vc.txt"), "");
    }

    // Deliver all deliverable delayed messages
    public void deliverDelayedMessages(String roomId) {
        List<Message> messages = new ArrayList<>(sr.getDelayedMessages(roomId));
        VectorClock vc = getCurrentVectorClock(roomId);

        int size = messages.size();
        while (!messages.isEmpty()) {
            Message msg = messages.getFirst();
            VectorClock newVC = msg.vectorClock();
            if (newVC.canBeDeliveredAfter(vc)) {
                vc = newVC;
                deliverMessage(roomId, msg);

                // Remove delivered data from delayed lists
                messages.removeFirst();
            } else break;
        }
        if (size == messages.size()) return;

        sw.overwrite(Paths.get(roomId, "delayed", "vector_clocks.txt"), listToText(
                messages.stream()
                        .map(Message::vectorClock)
                        .toList() // List<VectorClock>
        ));
        sw.overwrite(Paths.get(roomId, "delayed", "messages.txt"), listToText(
                messages.stream()
                        .map(Message::text)
                        .toList() // List<String>
        ));
    }

    // Returns all messages that could be delivered after a given message ~ may contain messages already delivered!!
    public List<Message> getMessagesAfter(String roomId, Message message) {
        VectorClock vc = message.vectorClock();
        List<Message> messages = new ArrayList<>(sr.getMessages(roomId));
        List<Message> delayed = new ArrayList<>(sr.getDelayedMessages(roomId));
        List<Message> result = new ArrayList<>();

        boolean flag = false;
        for (Message msg : messages) {
            if (flag) result.add(msg);
            else if (msg.vectorClock().equals(vc)) flag = true;
            else if (vc.isYoungerExceptForOne(msg.vectorClock()) && !msg.vectorClock().isOlder(vc)) {
                result.add(msg);
                flag = true;
            }
        }

        if (flag) result.addAll(delayed);
        else {
            for (Message msg : delayed) {
                if (flag) result.add(msg);
                else if (msg.vectorClock().equals(vc)) flag = true;
                else if (vc.isYoungerExceptForOne(msg.vectorClock()) && !msg.vectorClock().isOlder(vc)) {
                    result.add(msg);
                    flag = true;
                }
            }
        }

        return result;
    }

    // Absolutely nukes a path
    public void delete(String roomId) {
        sw.deleteDirectory(Paths.get(roomId));
    }

    // Converts a List of data to a string, elements are separated by \n
    private String listToText(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    // Method to check if a directory exists at PATH/roomName
    public boolean doesRoomExist(String roomName) {
        return sr.doesRoomExist(roomName);
    }

    // Returns a list of all room names
    public List<String> getRoomNames() {
        return sr.getDirectoriesUnderPath();
    }

    public int getIndex(String roomName, String username){
        List<Participant> participants = getParticipants(roomName);
        for (Participant p : participants){
            if (p.name().equals(username)){
                return p.index();
            }
        }
        return -1;
    }
    public void getChat(String roomName){
        List<Message> messages = sr.getMessages(roomName);
        for (Message m : messages){
            System.out.println(m.text());
        }
    }
}
