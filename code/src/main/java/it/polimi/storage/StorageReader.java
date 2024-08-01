package it.polimi.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class StorageReader {
    // Initialize the PATH variable, this is the directory where all files will be stored
    private final String PATH = String.valueOf(Paths.get(System.getProperty("user.home"), "chat_ss"));

    // Returns the last available vector clock (for delivered messages)
    public VectorClock getCurrentVectorClock(String roomId) {
        // Get the string containing the current vector clock
        Path location = Paths.get(PATH, roomId, "last_vc.txt");
        String vc = catFile(location).getFirst();

        // Parse the String to the corresponding vector clock
        return VectorClock.parseVectorClock(vc);
    }

    // Reads all lines from a file
    private List<String> catFile(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public List<VectorClock> getDelayedVectorClocks(String roomId) {
        Path location = Paths.get(PATH, roomId, "delayed", "vector_clocks.txt");
        return catFile(location).stream()
                .map(VectorClock::parseVectorClock)
                .toList();
    }

    public List<String> getDelayedMessages(String roomId){
        Path location = Paths.get(PATH, roomId, "delayed", "messages.txt");
        return catFile(location);
    }


}
