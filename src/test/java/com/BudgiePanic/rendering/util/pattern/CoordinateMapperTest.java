package com.BudgiePanic.rendering.util.pattern;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;

public class CoordinateMapperTest {

    @Test
    void testUSphereMap() {
        var tests = List.of(
            new Pair<>(makePoint(0, 0, -1), 0.0),
            new Pair<>(makePoint(1, 0, 0), 0.25),
            new Pair<>(makePoint(0, 0, 1), 0.5),
            new Pair<>(makePoint(-1, 0, 0), 0.75),
            new Pair<>(makePoint(0, 1, 0), 0.5),
            new Pair<>(makePoint(0, -1, 0), 0.5),
            new Pair<>(makePoint((Math.sqrt(2) / 2), (Math.sqrt(2) / 2), 0), 0.25)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.uSphereMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testVSphereMap() {
        var tests = List.of(
            new Pair<>(makePoint(0, 0, -1), 0.5),
            new Pair<>(makePoint(1, 0, 0), 0.5),
            new Pair<>(makePoint(0, 0, 1), 0.5),
            new Pair<>(makePoint(-1, 0, 0), 0.5),
            new Pair<>(makePoint(0, 1, 0), 1.0),
            new Pair<>(makePoint(0, -1, 0), 0.0),
            new Pair<>(makePoint((Math.sqrt(2) / 2), (Math.sqrt(2) / 2), 0), 0.75)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.vSphereMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }
}
