package it.polimi.Storage;

import it.polimi.Entities.Message;
import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import it.polimi.States.RoomStateManager;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

class StorageReader {
    // Initialize the PATH variable, this is the directory where all files will be stored
    private final String PATH = String.valueOf(Paths.get(System.getProperty("user.home"), "chat_ss", RoomStateManager.getInstance().getUsername()));

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

    public int getNumOfUsers(String roomId){
        return catFile(Paths.get(PATH, roomId, "usernames.txt")).size();
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

    // Returns a list of all messages that have not been sent yet
    public List<Message> getUnsentMessages(String roomId) {
        Iterator<String> messages = catFile(Paths.get(PATH,roomId, "unsent_msg.txt")).iterator();
        Iterator<String> vectorClocks = catFile(Paths.get(PATH,roomId, "unsent_vc.txt")).iterator();

        List<Message> unsentMessages = new ArrayList<>();
        while (messages.hasNext() && vectorClocks.hasNext()) {
            unsentMessages.add(new Message(messages.next(), VectorClock.parseVectorClock(vectorClocks.next())));
        }
        return unsentMessages;
    }

    // Returns a list of all messages for a given chat room
    public List<Message> getMessages(String roomId) {
        List<String> text = catFile(Paths.get(PATH, roomId, "messages.txt"));
        List<VectorClock> vectorClocks = catFile(Paths.get(PATH, roomId, "vector_clocks.txt")).stream()
                .map(VectorClock::parseVectorClock)
                .toList();

        return IntStream.range(0, Math.min(text.size(), vectorClocks.size()))
                .mapToObj(i -> new Message(text.get(i), vectorClocks.get(i)))
                .toList();
    }

    // Method to check if a room exists
    public boolean doesRoomExist(String roomName) {
        // Construct the path to the directory
        Path roomPath = Paths.get(PATH, roomName);

        // Check if the directory exists and is indeed a directory
        return Files.exists(roomPath) && Files.isDirectory(roomPath);
    }

    // Method to get the list of directories directly under the base PATH
    public List<String> getDirectoriesUnderPath() {
        Path basePath = Paths.get(PATH);
        List<String> directories = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(basePath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    directories.add(entry.getFileName().toString());
                }
            }
        } catch (IOException e) {
            if (!(e instanceof NoSuchFileException)) {
                System.err.println("Failed to read directories under " + basePath);
                e.printStackTrace();
            }
        }

        return directories;
    }
}
