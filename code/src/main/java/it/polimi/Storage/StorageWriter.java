package it.polimi.Storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;

class StorageWriter {
    private final Path PATH = Paths.get(System.getProperty("user.home"), "chat_ss");

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

    private void createMissingDirectories(Path relative) {
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

    public boolean directoryExists(Path location) {
        return Files.exists(location) && Files.isDirectory(location);
    }

    // delets a chat's directory
    public void deleteDirectory(Path relative) {
        Path location = sanitizePath(relative);
        try {
            Files.walk(location)
                    .sorted(Comparator.reverseOrder()) // have to first delete the files, then the subdirectories ...
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
}