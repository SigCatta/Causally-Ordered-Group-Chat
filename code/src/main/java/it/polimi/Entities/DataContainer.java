package it.polimi.Entities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public record DataContainer(ConcurrentHashMap<String, List<String>> roomsMap, Set<String> deletedRooms, ConcurrentHashMap<String, String> usersMap) implements Serializable {
}