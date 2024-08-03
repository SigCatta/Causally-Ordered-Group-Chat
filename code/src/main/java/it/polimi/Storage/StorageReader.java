package it.polimi.Storage;

import it.polimi.Entities.Message;
import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

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


    // Returns a list of all delayed messages for a given chat room
    public List<Message> getDelayedMessages(String roomId) {
        Path msgLocation = Paths.get(PATH, roomId, "delayed", "messages.txt");
        Path vcLocation = Paths.get(PATH, roomId, "delayed", "vector_clocks.txt");

        List<String> messages = catFile(msgLocation);
        List<VectorClock> vectorClocks = catFile(vcLocation).stream().map(VectorClock::parseVectorClock).toList();

        return IntStream.range(0, Math.min(messages.size(), vectorClocks.size()))
                .mapToObj(i -> new Message(messages.get(i), vectorClocks.get(i)))
                .toList();
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

    public List<Message> getUnsentMessages(String roomId) {
        Iterator<String> messages = catFile(Paths.get(roomId, "unsent_msg.txt")).iterator();
        Iterator<String> vectorClocks = catFile(Paths.get(roomId, "unsent_vc.txt")).iterator();

        List<Message> unsentMessages = new ArrayList<>();
        while (messages.hasNext() && vectorClocks.hasNext()) {
            unsentMessages.add(new Message(messages.next(), VectorClock.parseVectorClock(vectorClocks.next())));
        }
        return unsentMessages;
    }


}
