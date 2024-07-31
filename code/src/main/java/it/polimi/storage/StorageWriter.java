package it.polimi.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class StorageWriter {
    // Initialize the PATH variable, this is the directory where all files will be stored
    private final String PATH = System.getProperty("user.home") + "/chat_ss";

    // Appends data to a given file
    public void append(String location, String string) {
        if (fileExists(location)) {
            try (FileWriter writer = new FileWriter(adaptForWindows(PATH + "/" + location), true)) {
                writer.write(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creates a file and all the necessary directories to complete the path
    public boolean createFile(String location) {
        if (!fileExists(location)) {
            // If the location contains a non-existing directory, create it
            String dir = location.substring(0, location.lastIndexOf('/'));
            if (!directoryExists(dir)) {
                createMissingDirectories(dir);
            }

            // Finally create the file
            try {
                Files.createFile(Paths.get(adaptForWindows(PATH + "/" + location)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    // Creates missing directories to complete a given path
    private void createMissingDirectories(String location) {
        String[] directories = location.replace(PATH + '/', "").split("/");
        String dir = PATH;

        for (String subdir : directories) {
            dir += "/" + subdir;
            dir = adaptForWindows(dir);
            if (!directoryExists(dir)) {
                try {
                    Files.createDirectory(Paths.get(dir));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Deletes a file
    private void deleteFile(String location) {
        try {
            Files.deleteIfExists(Paths.get(adaptForWindows(PATH + "/" + location)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Checks if a file exists or not
    private boolean fileExists(String location) {
        return Files.exists(Paths.get(adaptForWindows(PATH + "/" + location)));
    }

    // Checks if a directory exists or not
    public boolean directoryExists(String location) {
        location = adaptForWindows(location);
        return Files.exists(Paths.get(location))
                && Files.isDirectory(Paths.get(location));
    }

    // Supports windows machines...
    private String adaptForWindows(String path) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            path = path.replace('/', '\\');
        }
        return path;
    }
}