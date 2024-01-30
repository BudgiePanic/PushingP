package com.BudgiePanic.rendering.util.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;

public class GradientTest {
    
    @Test
    void testGradientInterpolation() {
        var pattern = new Gradient(Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(new Color(0.75f,0.75f,0.75f), pattern.colorAt(Tuple.makePoint(0.25f, 0, 0)));
        assertEquals(new Color(0.5f,0.5f,0.5f), pattern.colorAt(Tuple.makePoint(0.50f, 0, 0)));
        assertEquals(new Color(0.25f,0.25f,0.25f), pattern.colorAt(Tuple.makePoint(0.75f, 0, 0)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(1, 0, 0)));
    }

}
