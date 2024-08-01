package it.polimi.Storage;

import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StableStorageTest {
    @Test
    void test() {
        Participant p1 = new Participant("1", "1.1.1.1");
        Participant p2 = new Participant("2", "2.2.2.2");
        Participant p3 = new Participant("3", "3.3.3.3");

        StableStorage ss = new StableStorage();
        ss.initNewRoom("test", List.of(p1, p2, p3));

        ss.delayMessage("test", "Message 3", new VectorClock(List.of(1, 1, 1)));
        ss.deliverMessage("test", "Message 1", new VectorClock(List.of(1, 0, 0)));
        ss.deliverMessage("test", "Message 2", new VectorClock(List.of(1, 1, 0)));

        ss.deliverDelayedMessages("test");

        ss.delete("test");
    }
}
