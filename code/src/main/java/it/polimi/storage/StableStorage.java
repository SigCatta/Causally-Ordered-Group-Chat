package it.polimi.storage;

import java.util.Arrays;
import java.util.List;

public class StableStorage {
    private final StorageWriter sw;
    private final StorageReader sr;

    public StableStorage() {
        this.sw = new StorageWriter();
        this.sr = new StorageReader();
    }

    public void initNewRoom(String roomId, int size) {
        // Create necessary files
        sw.createFile(roomId + "/messages.txt");
        sw.createFile(roomId + "/vector_clocks.txt");
        sw.createFile(roomId + "/last_vc.txt");

        // Initialize files
        sw.append(roomId + "/messages.txt", "Chat room created successfully");

        StringBuilder sb = new StringBuilder() // Build the first vector clock: (0,0,...,0,0)
                .append("[")
                .repeat("0,", size - 1)
                .append("0]\n");
        sw.append(roomId + "/vector_clocks.txt", sb.toString());
        sw.append(roomId + "/last_vc.txt", sb.toString());
    }

    public void persistMessage(String roomId, String message, VectorClock vc) {

        sw.append(roomId + "/messages.txt", message.replace("\n", " "));
    }



}
