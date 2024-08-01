package it.polimi.storage.it.polimi;

import it.polimi.storage.StableStorage;
import it.polimi.storage.VectorClock;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StableStorageTest {
    @Test
    void test (){

        StableStorage ss = new StableStorage();
        ss.initNewRoom("test", 3);

        ss.delayMessage("test", "Message 3", new VectorClock(List.of(1, 1, 1)));
        ss.deliverMessage("test", "Message 1", new VectorClock(List.of(1, 0, 0)));
        ss.deliverMessage("test", "Message 2", new VectorClock(List.of(1, 1, 0)));

        ss.deliverDelayedMessages("test");
    }
}
