package it.polimi.storage;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VectorClockTest {
    @Test
    void testCanBeDelivered() {
        VectorClock vc1 = new VectorClock(List.of(0, 0, 0));
        VectorClock vc2 = new VectorClock(List.of(1, 0, 0));
        VectorClock vc3 = new VectorClock(List.of(1, 1, 0));
        VectorClock vc4 = new VectorClock(List.of(1, 0, 1));
        VectorClock vcf = new VectorClock(List.of(1, 2, 3));

        VectorClock vce = new VectorClock(List.of(0, 0, 0, 0));

        // vc1 is the most recent delivery
        assertTrue(vc2.canBeDelivered(vc1));
        assertFalse(vc3.canBeDelivered(vc1));
        assertFalse(vc4.canBeDelivered(vc1));
        assertFalse(vcf.canBeDelivered(vc1));
        assertThrows(Exception.class, () -> vce.canBeDelivered(vc1));


        // vc2 is the most recent delivery
        assertTrue(vc3.canBeDelivered(vc2));
        assertTrue(vc4.canBeDelivered(vc2));
        assertFalse(vcf.canBeDelivered(vc2));
        assertThrows(Exception.class, () -> vce.canBeDelivered(vc2));

        // vc3 is the most recent delivery
        assertTrue(vc4.canBeDelivered(vc3));
        assertFalse(vcf.canBeDelivered(vc3));
        assertThrows(Exception.class, () -> vce.canBeDelivered(vc3));

        // vc4 is the most recent delivery
        assertTrue(vc3.canBeDelivered(vc4));
        assertFalse(vcf.canBeDelivered(vc4));
        assertThrows(Exception.class, () -> vce.canBeDelivered(vc4));

        System.out.println(vc3);
    }
}
