package com.BudgiePanic.rendering.util.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Pair;

public class Pattern2DTest {
    
    @Test
    void testChecker() {
        var tests = List.of(
            new Pair<>(new Pair<>(0.0, 0.0), Colors.black),
            new Pair<>(new Pair<>(0.5, 0.0), Colors.white),
            new Pair<>(new Pair<>(0.0, 0.5), Colors.white),
            new Pair<>(new Pair<>(0.5, 0.5), Colors.black),
            new Pair<>(new Pair<>(1.0, 1.0), Colors.black),
            new Pair<>(new Pair<>(0.25, 0.25), Colors.black),
            new Pair<>(new Pair<>(0.75, 0.75), Colors.black),
            new Pair<>(new Pair<>(0.75, 0.25), Colors.white),
            new Pair<>(new Pair<>(0.25, 0.75), Colors.white)
        );
        var pattern = Pattern2D.checker(2, 2, Pattern2D.solidColor(Colors.black), Pattern2D.solidColor(Colors.white));
        for (final var test : tests) {
            double u = test.a().a();
            double v = test.a().b();
            var expected = test.b();
            var result = pattern.sample(u, v);
            assertEquals(expected, result, test.toString());
        }
    }

    @Test
    void testSolidColor() {
        var pattern = Pattern2D.solidColor(Colors.blue);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                var result = pattern.sample(x, y);
                assertEquals(Colors.blue, result, "x " + x + " y " + y);
            }
        }
    }
}
