package it.polimi.storage;

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
        sw.createFile(roomId + "/messages.txt");
        sw.createFile(roomId + "/vector_clocks.txt");
        sw.createFile(roomId + "/last_vc.txt");

        // Initialize files
        sw.append(roomId + "/messages.txt", "Chat room created successfully");

        StringBuilder sb = new StringBuilder() // Build the first vector clock: (0,0,...,0,0)
                .append("[");
        // .repeat("0,", size - 1)
        // .append("0,".repeat(size - 1));
        for (int i = 0; i < size - 1; i++) {
            sb.append("0,");
        }
        sb.append("0]\n");
        sw.append(roomId + "/vector_clocks.txt", sb.toString());
        sw.append(roomId + "/last_vc.txt", sb.toString());
    }

    // Returns the current vector clock
    public VectorClock getCurrentVectorClock(String roomId) {
        return sr.getCurrentVectorClock(roomId);
    }

    // Saves a message to stable storage, along with the vector clock
    public void persistMessage(String roomId, String message, VectorClock vc) {

        sw.append(roomId + "/messages.txt", message.replace("\n", " "));
    }

}
