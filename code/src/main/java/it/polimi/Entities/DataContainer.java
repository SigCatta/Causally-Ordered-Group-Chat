package it.polimi.Entities;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

public record DataContainer(ConcurrentHashMap<String, List<String>> roomsMap, Set<String> deletedRooms, ConcurrentHashMap<String, String> usersMap) implements Serializable {
}