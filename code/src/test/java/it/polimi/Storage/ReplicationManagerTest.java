package it.polimi.Storage;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class ReplicationManagerTest {
    @Test
    public void test() {
        ReplicationManager replicationManager = new ReplicationManager();
        replicationManager.becomeRoomsNode(new ArrayList<>(), new ConcurrentHashMap<>());

        replicationManager.addRoom("roomId", List.of("u1", "u2"));
        assertTrue(replicationManager.getRoomsDiff(new ArrayList<>()).contains("roomId"));

        assertTrue(replicationManager.getParticipants("roomId").containsAll(List.of("u1", "u2")));

        replicationManager.deleteRoom("roomId");
        assertTrue(replicationManager.getDeletedRooms(new ArrayList<>()).contains("roomId"));

        assertNull(replicationManager.getParticipants("roomId"));
    }
}
