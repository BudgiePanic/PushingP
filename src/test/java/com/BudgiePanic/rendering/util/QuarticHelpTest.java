package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class QuarticHelpTest {
    @Test
    void testSolveQuartic() {
        var tests = List.of(
            new Pair<>(new float[]{1f/14f, 1f/14f, -13f/14f, -1f/14f, 1.35714f}, List.of(-3.93, -1.26, 1.33, 2.85)),
            new Pair<>(new float[]{1f, 1f, 1f, 1f, -1f}, List.of(-1.290, 0.518)),
            new Pair<>(new float[]{-2f, -4f, 7f, 4f, 0f}, List.of(-2.955, -0.473, 0.0, 1.428)),
            new Pair<>(new float[]{1f, 1f, 1f, 1f, -10f}, List.of(-2, 1.402)),
            new Pair<>(new float[]{1f, -8f, 18f, -9f, -10f}, List.of(-0.498, 4.637)),
            new Pair<>(new float[]{2f, 8f, 7f, 4f, -10f}, List.of(-3.258, 0.718))
        );
        for (var test : tests) {
            var a = test.a(); // coefficients
            var b = test.b(); // expected roots
            var foundRoots = QuarticHelp.solveQuartic(a[0], a[1], a[2], a[3], a[4]);
            assertEquals(b.size(), foundRoots.size());
            for (int i = 0; i < foundRoots.size(); i++) {
                var expected = b.get(i);
                var actual = foundRoots.get(i);
                assertTrue(FloatHelp.compareFloat(expected.floatValue(), actual) == 0, "expected: " + expected + " actual: " + actual);
            }
        }
    }
}
