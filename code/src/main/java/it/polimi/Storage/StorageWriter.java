package it.polimi.Storage;

import it.polimi.States.RoomStateManager;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

class StorageWriter {
    private final Path PATH = Paths.get(System.getProperty("user.home"), "chat_ss", RoomStateManager.getInstance().getUsername());

    // Appends a String to a file
    public void append(Path relative, String string) {
        Path location = sanitizePath(relative);
        if (Files.exists(location)) {
            try (FileWriter writer = new FileWriter(location.toString(), true)) {
                writer.write(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Overwrites a file, effectively deleting all contents
    public void overwrite(Path relative, String string) {
        Path location = sanitizePath(relative);
        if (Files.exists(location)) {
            try (FileWriter writer = new FileWriter(location.toString())) {
                writer.write(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creates a file at a give position
    public void createFile(Path relative) {
        Path location = sanitizePath(relative);
        if (Files.notExists(location)) {
            createMissingDirectories(relative);
            try {
                Files.createFile(location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creates all the missing directories in a path
    private void createMissingDirectories(Path relative) {
        createBaseDirectories();
        Iterator<Path> directories = relative.iterator();
        Path dir = PATH;

        while (true) {
            dir = dir.resolve(directories.next());
            if (!directories.hasNext()) break; // needed to stop before the last element ~ which is a file

            if (!directoryExists(dir)) {
                try {
                    Files.createDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // True if the indicated directory exists
    public boolean directoryExists(Path location) {
        return Files.exists(location) && Files.isDirectory(location);
    }

    // Deletes a chat's directory
    public void deleteDirectory(Path relative) {
        Path location = sanitizePath(relative);
        try (Stream<Path> paths = Files.walk(location)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Assures the path is within the chat_ss directory
    private Path sanitizePath(Path relative) {
        Path location = PATH.resolve(relative).normalize();
        if (!location.startsWith(PATH)) {
            throw new IllegalArgumentException("Invalid path: " + relative);
        }
        return location;
    }

    // Creates the all directories needed in the PATH constant
    private void createBaseDirectories() {
        Path chatRoot = Paths.get(System.getProperty("user.home"), "chat_ss");

        if (!Files.exists(chatRoot)) {
            try {
                Files.createDirectory(chatRoot);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(PATH)) {
            try {
                Files.createDirectory(PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Path getPATH() {
        return PATH;
    }
}