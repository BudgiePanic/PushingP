package com.BudgiePanic.rendering.util.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.checker;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;

public class CheckerTest {
 
    @Test
    void testCheckerPatternX() {
        var pattern = new BiPattern(checker, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0.99f, 0, 0)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(1.01f, 0, 0)));
    }
    
    @Test
    void testCheckerPatternY() {
        var pattern = new BiPattern(checker, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0.99f, 0)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(0, 1.01f, 0)));
    }

    @Test
    void testCheckerPatternZ() {
        var pattern = new BiPattern(checker, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0.99f)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(0, 0, 1.01f)));
    }
}
