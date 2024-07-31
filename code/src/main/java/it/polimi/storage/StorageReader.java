package it.polimi.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class StorageReader {
    // Initialize the PATH variable, this is the directory where all files will be stored
    private final String PATH = String.valueOf(Paths.get(System.getProperty("user.home"), "chat_ss"));

    public VectorClock getLatestVectorClock(String roomId) {
        Path location = Paths.get(PATH, roomId, "last_vc.txt");
        String vc = catFile(location).getFirst();

        List<Integer> vector =
                Arrays.stream(vc.substring(1, vc.length() - 1) // Remove brackets
                                .replace(" ", "")
                                .split(","))
                        .map(Integer::valueOf)
                        .toList();
        return new VectorClock(vector);
    }

    private List<String> catFile(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
