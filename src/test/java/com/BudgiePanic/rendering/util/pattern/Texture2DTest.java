package com.BudgiePanic.rendering.util.pattern;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.io.CanvasReader;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;

public class Texture2DTest {
    @Test
    void testSample() {
        ClassLoader classLoader = getClass().getClassLoader();
        var e = classLoader.getResource("test_image.ppm");
        Assumptions.assumeTrue(e != null, "Could not find image \"test_image.ppm\" skipping texture sampling test");
        File file = new File(e.getFile());
        assertDoesNotThrow(() -> {
            Canvas image = CanvasReader.createCanvas(file);
            Texture2D texture = new Texture2D(image);
            var tests = List.of(
                new Pair<>(new Pair<>(0.0, 0.0), new Color(0.9, 0.9, 0.9)),
                new Pair<>(new Pair<>(0.3, 0.0), new Color(0.2, 0.2, 0.2)),
                new Pair<>(new Pair<>(0.6, 0.3), new Color(0.1, 0.1, 0.1)),
                new Pair<>(new Pair<>(1.0, 1.0), new Color(0.9, 0.9, 0.9)),
                new Pair<>(new Pair<>(0.9, 1.0), new Color(0.8, 0.8, 0.8)),
                new Pair<>(new Pair<>(0.0, 1.0), new Color(0.0, 0.0, 0.0))
            );
            for (final var test : tests) {
                var expected = test.b();
                var actual = texture.sample(test.a().a(), test.a().b());
                assertEquals(expected, actual, test.toString());
            }
        });
    }
}
