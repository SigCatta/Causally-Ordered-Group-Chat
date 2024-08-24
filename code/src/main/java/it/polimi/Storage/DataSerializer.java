package it.polimi.Storage;

import it.polimi.Entities.DataContainer;

import java.io.*;
import java.nio.file.Path;

public class DataSerializer {
    public static void serializeData(Path filePath, DataContainer data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toString()))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataContainer deserializeData(Path filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toString()))) {
            return (DataContainer) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            if (e instanceof EOFException) return null;
            e.printStackTrace();
            return null;
        }
    }
}