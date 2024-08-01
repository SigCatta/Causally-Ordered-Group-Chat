package it.polimi.storage;

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
    public void initNewRoom(String roomId, int size) {
        if (size < 2) {
            System.out.println("Go get some friends...");
            throw new IllegalArgumentException("Room size must be at least 2");
        }
        // Create necessary files
        sw.createFile(Paths.get(roomId, "messages.txt"));
        sw.createFile(Paths.get(roomId, "vector_clocks.txt"));
        sw.createFile(Paths.get(roomId, "last_vc.txt"));

        sw.createFile(Paths.get(roomId, "delayed", "messages.txt"));
        sw.createFile(Paths.get(roomId, "delayed", "vector_clocks.txt"));

        // Initialize files
        sw.append(Paths.get(roomId, "messages.txt"), "Chat room created successfully\n");

        StringBuilder sb = new StringBuilder() // Build the first vector clock: (0,0,...,0,0)
                .append("[")
                .repeat("0, ", size - 1)
                .append("0]\n");
        sw.append(Paths.get(roomId, "vector_clocks.txt"), sb.toString());
        sw.append(Paths.get(roomId, "last_vc.txt"), sb.toString());
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
    public void delayMessage(String roomId, String message, VectorClock newVC) {
        sw.append(Paths.get(roomId, "delayed", "vector_clocks.txt"), newVC.toString() + '\n');
        sw.append(Paths.get(roomId, "delayed", "messages.txt"), message.replace("\n", " ") + '\n');
    }

    public void deliverDelayedMessages(String roomId) {
        List<String> messages = new ArrayList<>(sr.getDelayedMessages(roomId));
        List<VectorClock> vectorClocks = new ArrayList<>(sr.getDelayedVectorClocks(roomId));

        VectorClock vc = getCurrentVectorClock(roomId);

        for (int i = 0; i < vectorClocks.size(); i++) {
            VectorClock newVC = vectorClocks.getFirst();
            if (newVC.canBeDelivered(vc)) {
                vc = newVC;
                deliverMessage(roomId, messages.getFirst(), newVC);

                // Remove delivered data from delayed lists
                vectorClocks.removeFirst();
                messages.removeFirst();
            } else break;
        }

        sw.overwrite(Paths.get(roomId, "delayed", "vector_clocks.txt"), listToText(vectorClocks));
        sw.overwrite(Paths.get(roomId, "delayed", "messages.txt"), listToText(messages));
    }

    private String listToText(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

}
