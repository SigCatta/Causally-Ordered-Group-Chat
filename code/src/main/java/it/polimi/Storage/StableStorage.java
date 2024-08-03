package it.polimi.Storage;

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
    public void deliverMessage(String roomId, String message, VectorClock newVC) {
        VectorClock currentVC = getCurrentVectorClock(roomId);

        sw.append(Paths.get(roomId, "vector_clocks.txt"), newVC.toString() + '\n');
        sw.append(Paths.get(roomId, "messages.txt"), message.replace("\n", " ") + '\n');
        sw.overwrite(Paths.get(roomId, "last_vc.txt"), currentVC.merge(newVC).toString());
    }

    // DELAYED - saves a message to stable storage, along with the vector clock
    // messages are stored in order based on their vector clocks
    public void delayMessage(String roomId, String message, VectorClock newVC) {
        List<VectorClock> vectorClocks = sr.getDelayedVectorClocks(roomId);
        List<String> messages = sr.getDelayedMessages(roomId);
        StringBuilder vcSB = new StringBuilder();
        StringBuilder msgSB = new StringBuilder();
        boolean ok = false;

        // If there are no other delayed messages OR the new vc is older than all other saved, just insert the new message in front
        if (vectorClocks.isEmpty() || newVC.isOlder(vectorClocks.getFirst())) {
            vcSB.append(newVC).append('\n');
            msgSB.append(message.replace("\n", " ")).append('\n');
            ok = true;
        }

        for (int i = 0; i < vectorClocks.size(); i++) {
            vcSB.append(vectorClocks.get(i)).append('\n');
            msgSB.append(messages.get(i).replace("\n", " ")).append('\n');
            if (newVC.canBeDeliveredAfter(vectorClocks.get(i)) && !ok) {
                vcSB.append(newVC).append('\n');
                msgSB.append(message.replace("\n", " ")).append('\n');
                ok = true;
            }
        }

        if (!ok) {
            vcSB.append(newVC).append('\n');
            msgSB.append(message.replace("\n", " ")).append('\n');
        }

        sw.overwrite(Paths.get(roomId, "delayed", "vector_clocks.txt"), vcSB.toString());
        sw.overwrite(Paths.get(roomId, "delayed", "messages.txt"), msgSB.toString());
    }

    // Deliver all deliverable delayed messages
    public void deliverDelayedMessages(String roomId) {
        List<String> messages = new ArrayList<>(sr.getDelayedMessages(roomId));
        List<VectorClock> vectorClocks = new ArrayList<>(sr.getDelayedVectorClocks(roomId));

        VectorClock vc = getCurrentVectorClock(roomId);
        int size = vectorClocks.size();
        for (int i = 0; i < size; i++) {
            VectorClock newVC = vectorClocks.getFirst();
            if (newVC.canBeDeliveredAfter(vc)) {
                vc = newVC;
                deliverMessage(roomId, messages.getFirst(), newVC);

                // Remove delivered data from delayed lists
                vectorClocks.removeFirst();
                messages.removeFirst();
            } else {
                if (i == 0) return;
                break;
            }
        }

        sw.overwrite(Paths.get(roomId, "delayed", "vector_clocks.txt"), listToText(vectorClocks));
        sw.overwrite(Paths.get(roomId, "delayed", "messages.txt"), listToText(messages));
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
}