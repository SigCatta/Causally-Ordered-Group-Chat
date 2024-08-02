package it.polimi.Storage;

import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class StorageReader {
    // Initialize the PATH variable, this is the directory where all files will be stored
    private final String PATH = String.valueOf(Paths.get(System.getProperty("user.home"), "chat_ss"));

    // Reads all lines from a file
    private List<String> catFile(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    // Returns the current vector clock for a chat room (for delivered messages)
    public VectorClock getCurrentVectorClock(String roomId) {
        // Get the string containing the current vector clock
        Path location = Paths.get(PATH, roomId, "last_vc.txt");
        String vc = catFile(location).getFirst();

        // Parse the String to the corresponding vector clock
        return VectorClock.parseVectorClock(vc);
    }

    // Returns a list of all delayed vector clocks for a given chat room
    public List<VectorClock> getDelayedVectorClocks(String roomId) {
        Path location = Paths.get(PATH, roomId, "delayed", "vector_clocks.txt");
        return catFile(location).stream()
                .map(VectorClock::parseVectorClock)
                .toList();
    }

    // Returns a list of all delayed messages for a given chat room
    public List<String> getDelayedMessages(String roomId) {
        Path location = Paths.get(PATH, roomId, "delayed", "messages.txt");
        return catFile(location);
    }

    // Returns the list of participants for a given chat room
    public List<Participant> getParticipants(String roomId) {
        Iterator<String> usernames = catFile(Paths.get(PATH, roomId, "usernames.txt")).iterator();
        Iterator<String> ipAddresses = catFile(Paths.get(PATH, roomId, "addresses.txt")).iterator();
        int i = 0; // index, counting the line at which each participant is at in the files

        List<Participant> participants = new ArrayList<>();
        while (usernames.hasNext() && ipAddresses.hasNext()) {
            participants.add(new Participant(i++, usernames.next(), ipAddresses.next()));
        }
        return participants;
    }


}
