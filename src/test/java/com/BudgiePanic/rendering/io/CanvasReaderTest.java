package com.BudgiePanic.rendering.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CanvasReaderTest {
    @Test
    void testCheckMagic() {
        assertFalse(CanvasReader.checkMagic(List.of()));
        assertFalse(CanvasReader.checkMagic(List.of("")));
        assertFalse(CanvasReader.checkMagic(List.of("p3")));
        assertFalse(CanvasReader.checkMagic(List.of("P32")));
        assertFalse(CanvasReader.checkMagic(List.of("", "P3")));
    }

    @Test 
    void testCanvasDimensions() {
        var file = List.of(
            "P3",
            "10 2",
            "255"
        );
        var result = CanvasReader.checkDimensions(file);
        assertTrue(result.isPresent());
        int width = result.get().a();
        int height = result.get().b();
        assertEquals(10, width);
        assertEquals(2, height);
    }
}
