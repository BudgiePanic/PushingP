package com.BudgiePanic.rendering.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;

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

    @Test
    void testCanvasReaderA() {
        assertDoesNotThrow(() -> {
            var result = CanvasReader.parseLines(List.of("P3","10 2","255",
            "0 0 0  0 0 0  0 0 0  0 0 0  0 0 0",
            "0 0 0  0 0 0  0 0 0  0 0 0  0 0 0",
            "0 0 0  0 0 0  0 0 0  0 0 0  0 0 0",
            "0 0 0  0 0 0  0 0 0  0 0 0  0 0 0"));
            assertEquals(10, result.getWidth());
            assertEquals(2, result.getHeight());
        });
    }

    @Test
    void testCanvasReaderB() {
        assertDoesNotThrow(() -> {
            var result = CanvasReader.parseLines(List.of("P3","4 3","255",
            "255 127 0  0 127 255  127 255 0  255 255 255",
            "0 0 0  255 0 0  0 255 0  0 0 255",
            "255 255 0  0 255 255  255 0 255  127 127 127"));
            assertEquals(4, result.getWidth());
            assertEquals(3, result.getHeight());

            var tests = List.of(
                new Pair<>(new Pair<>(0, 0), new Color(1, 0.498, 0)),
                new Pair<>(new Pair<>(1, 0), new Color(0, 0.498, 1)),
                new Pair<>(new Pair<>(2, 0), new Color(0.498, 1, 0)),
                new Pair<>(new Pair<>(3, 0), new Color(1, 1, 1)),
                new Pair<>(new Pair<>(0, 1), new Color(0, 0, 0)),
                new Pair<>(new Pair<>(1, 1), new Color(1, 0, 0)),
                new Pair<>(new Pair<>(2, 1), new Color(0, 1, 0)),
                new Pair<>(new Pair<>(3, 1), new Color(0, 0, 1)),
                new Pair<>(new Pair<>(0, 2), new Color(1, 1, 0)),
                new Pair<>(new Pair<>(1, 2), new Color(0, 1, 1)),
                new Pair<>(new Pair<>(2, 2), new Color(1, 0, 1)),
                new Pair<>(new Pair<>(3, 2), new Color(0.498, 0.498, 0.498))
            );

            for (final var test : tests) {
                var expected = test.b();
                var actual = result.getPixel(test.a().a(), test.a().b());
                assertEquals(expected, actual);
            }
        });
    }

    @Test
    void testCommentParsing() {
        assertDoesNotThrow(() -> {
            var result = CanvasReader.parseLines(List.of("P3", "# comment","2 1","#another comment", "255", "#",
            "255 255 255",
            "# decoy",
            "255 0 255"));
            assertEquals(2, result.getWidth());
            assertEquals(1, result.getHeight());

            assertEquals(new Color(1, 1, 1), result.getPixel(0, 0));
            assertEquals(new Color(1, 0, 1), result.getPixel(1, 0));
        });
    }
}
 