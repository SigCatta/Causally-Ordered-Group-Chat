package it.polimi.Storage;

import it.polimi.Entities.Message;
import it.polimi.Entities.Participant;
import it.polimi.Entities.VectorClock;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StableStorageTest {
    @Test
    void threeDelayedAllDelivered() {
        Participant p1 = new Participant(0, "1", "1.1.1.1");
        Participant p2 = new Participant(1, "2", "2.2.2.2");
        Participant p3 = new Participant(2, "3", "3.3.3.3");

        StableStorage ss = new StableStorage();
        ss.initNewRoom("test", List.of(p1, p2, p3));

        ss.delayMessage("test", new Message("Message 5", new VectorClock(List.of(2, 2, 1))));
        ss.delayMessage("test", new Message("Message 3", new VectorClock(List.of(1, 1, 1))));
        ss.delayMessage("test", new Message("Message 4", new VectorClock(List.of(1, 2, 1))));
        // breakpoint the next line to check if the vector clocks are in the correct order in /delayed/vector_clocks.txt
        ss.deliverMessage("test", new Message("Message 1", new VectorClock(List.of(1, 0, 0))));
        ss.deliverMessage("test", new Message("Message 2", new VectorClock(List.of(1, 1, 0))));

        // breakpoint the next line to check if the messages are in the correct order in /messages.txt
        ss.deliverDelayedMessages("test");

        // breakpoint the next line to check if the files in the /delivered directory are empty and all other files are ordered correctly
        ss.delete("test");
    }

    @Test
    void fourDelayedThreeDelivered() {
        Participant p1 = new Participant(0, "1", "1.1.1.1");
        Participant p2 = new Participant(1, "2", "2.2.2.2");
        Participant p3 = new Participant(2, "3", "3.3.3.3");

        StableStorage ss = new StableStorage();
        ss.initNewRoom("test", List.of(p1, p2, p3));

        ss.delayMessage("test", new Message("Message 5", new VectorClock(List.of(2, 2, 1))));
        ss.delayMessage("test", new Message("Message 3", new VectorClock(List.of(1, 1, 1))));
        ss.delayMessage("test", new Message("Message 4", new VectorClock(List.of(1, 2, 1))));
        ss.delayMessage("test", new Message("Message 7", new VectorClock(List.of(3, 3, 3))));
        // breakpoint the next line to check if the vector clocks are in the correct order in /delayed/vector_clocks.txt
        ss.deliverMessage("test", new Message("Message 1", new VectorClock(List.of(1, 0, 0))));
        ss.deliverMessage("test", new Message("Message 2", new VectorClock(List.of(1, 1, 0))));

        // breakpoint the next line to check if the messages are in the correct order in /messages.txt
        ss.deliverDelayedMessages("test");

        // breakpoint the next line to check if the files in the /delivered directory are empty and all other files are ordered correctly
        ss.delete("test");
    }
}
