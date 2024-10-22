/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;

/**
 * Tests for verifying the functionality of the *.ppm file importer
 */
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

    @Test
    void testMultiLineColors() {
        assertDoesNotThrow(() -> {
            var result = CanvasReader.parseLines(List.of("P3", "1 1", "255",
            "51",
            "153",
            "",
            "204"));
            assertEquals(1, result.getWidth());
            assertEquals(1, result.getHeight());

            assertEquals(new Color(0.2, 0.6, 0.8), result.getPixel(0, 0));
        });
    }

    @Test
    void testAlternateRangeMapper() {
        assertDoesNotThrow(() -> {
            var result = CanvasReader.parseLines(List.of("P3", "2 2", "100",
            "100 100 100 50 50 50",
            "75 50 25  0 0 0"));
            assertEquals(2, result.getWidth());
            assertEquals(2, result.getHeight());
             assertEquals(new Color(0.75, 0.5, 0.25), result.getPixel(0, 1));
        });
    }
}
