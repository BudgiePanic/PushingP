package com.BudgiePanic.rendering.util.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;

public class RingTest {
    
    @Test
    void testRing() {
        var pattern = new Ring(Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(1, 0, 0)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(0, 0, 1)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(0.708f, 0, 0.708f)));
    }

}
