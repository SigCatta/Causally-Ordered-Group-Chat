package it.polimi.Storage;


import it.polimi.Entities.VectorClock;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class VectorClockTest {
    @Test
    public void testCanBeDeliveredAfter() {
        VectorClock vc1 = new VectorClock(List.of(0, 0, 0));
        VectorClock vc2 = new VectorClock(List.of(1, 0, 0));
        VectorClock vc3 = new VectorClock(List.of(1, 1, 0));
        VectorClock vc4 = new VectorClock(List.of(1, 0, 1));
        VectorClock vcf = new VectorClock(List.of(1, 2, 3));

        VectorClock vce = new VectorClock(List.of(0, 0, 0, 0));

        // vc1 is the most recent delivery
        assertTrue(vc2.canBeDeliveredAfter(vc1));
        assertFalse(vc3.canBeDeliveredAfter(vc1));
        assertFalse(vc4.canBeDeliveredAfter(vc1));
        assertFalse(vcf.canBeDeliveredAfter(vc1));
        assertThrows(Exception.class, () -> vce.canBeDeliveredAfter(vc1));


        // vc2 is the most recent delivery
        assertTrue(vc3.canBeDeliveredAfter(vc2));
        assertTrue(vc4.canBeDeliveredAfter(vc2));
        assertFalse(vcf.canBeDeliveredAfter(vc2));
        assertThrows(Exception.class, () -> vce.canBeDeliveredAfter(vc2));

        // vc3 is the most recent delivery
        assertTrue(vc4.canBeDeliveredAfter(vc3));
        assertFalse(vcf.canBeDeliveredAfter(vc3));
        assertThrows(Exception.class, () -> vce.canBeDeliveredAfter(vc3));

        // vc4 is the most recent delivery
        assertTrue(vc3.canBeDeliveredAfter(vc4));
        assertFalse(vcf.canBeDeliveredAfter(vc4));
        assertThrows(Exception.class, () -> vce.canBeDeliveredAfter(vc4));

        System.out.println(vc3);
    }

    @Test
    public void testMerge(){
        VectorClock vc1 = new VectorClock(List.of(0, 0, 0));
        VectorClock vc2 = new VectorClock(List.of(1, 0, 0));

        assertEquals(new VectorClock(List.of(1, 0, 0)), vc1.merge(vc2));
    }
}
