package it.polimi.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

class StorageWriter {
    // Initialize the PATH variable, this is the directory where all files will be stored
    private final Path PATH = Paths.get(System.getProperty("user.home"), "chat_ss");

    // Appends data to a given file
    public void append(Path relative, String string) {
        Path location = PATH.resolve(relative);
        if (Files.exists(location)) {
            try (FileWriter writer = new FileWriter(location.toString(), true)) {
                writer.write(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void overwrite(Path relative, String string) {
        Path location = PATH.resolve(relative);
        if (Files.exists(location)) {
            try (FileWriter writer = new FileWriter(location.toString())) {
                writer.write(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creates a file and all the necessary directories to complete the path
    public void createFile(Path relative) {
        Path location = PATH.resolve(relative);
        if (Files.notExists(location)) {
            // If the location contains a non-existing directory, create it
            createMissingDirectories(relative);

            // Finally create the file
            try {
                Files.createFile(location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creates missing directories to complete a given path
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

    // Deletes a file
    public void deleteFile(Path relative) {
        Path location = PATH.resolve(relative);
        try {
            Files.deleteIfExists(PATH.resolve(location));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Checks if a directory exists or not
    public boolean directoryExists(Path location) {
        return Files.exists(location)
                && Files.isDirectory(location);
    }

}