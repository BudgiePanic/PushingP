package com.BudgiePanic.rendering.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CanvasReaderTest {
    @Test
    void testCheckMagic() {
        assertThrows(CanvasReader.ParsingException.class, () -> { CanvasReader.parseLines(List.of()); });
        assertThrows(CanvasReader.ParsingException.class, () -> { CanvasReader.parseLines(List.of("")); });
        assertThrows(CanvasReader.ParsingException.class, () -> { CanvasReader.parseLines(List.of("p3")); });
        assertThrows(CanvasReader.ParsingException.class, () -> { CanvasReader.parseLines(List.of("", "P3")); });
    }

    @Test 
    void testCanvasDimensions() {
        var parser = new CanvasReader.DimensionParser(null);
        assertDoesNotThrow(() -> {
            parser.consumeLine("10 2");
            assertEquals(10, parser.width.get());
            assertEquals(2, parser.height.get());
        });
        assertThrows(CanvasReader.ParsingException.class, () -> { parser.consumeLine("not a line"); });
    }
}
