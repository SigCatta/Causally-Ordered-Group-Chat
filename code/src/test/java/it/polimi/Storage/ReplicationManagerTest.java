package it.polimi.Storage;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class ReplicationManagerTest {
    @Test
    public void test() {
        ReplicationManager replicationManager = ReplicationManager.getInstance();
        replicationManager.becomeRoomsNode(new ArrayList<>(), new ConcurrentHashMap<>());

        replicationManager.addRoom("roomId", List.of("u1", "u2"));
        assertTrue(replicationManager.getRoomsDiff(new ArrayList<>()).contains("roomId"));

        assertTrue(replicationManager.getParticipants("roomId").containsAll(List.of("u1", "u2")));

        replicationManager.deleteRoom("roomId");
        assertTrue(replicationManager.getDeletedRooms(new ArrayList<>()).contains("roomId"));

        assertNull(replicationManager.getParticipants("roomId"));
    }

    @Test
    public void choiceTest() {
        ReplicationManager replicationManager = ReplicationManager.getInstance();

        List<String> arr = new ArrayList<>(26);

        arr.add("node1");
        arr.add("node1");
        arr.add("node1");

        replicationManager.setRoomNodes(arr);
        replicationManager.setUserNodes(arr);

        assertEquals("node1", replicationManager.getRoomNodes().get(1));
        assertEquals("node1", replicationManager.chooseRoomNodeToHelp());
        assertEquals("node1", replicationManager.getUserNodes().get(1));
        assertEquals("node1", replicationManager.chooseUserNodeToHelp());
    }
}
