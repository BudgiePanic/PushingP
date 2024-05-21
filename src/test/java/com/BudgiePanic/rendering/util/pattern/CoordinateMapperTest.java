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
            var actual = CoordinateMapper.sphere.uSphereMap(test.a());
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
            var actual = CoordinateMapper.sphere.vSphereMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testUPlanarMap() {
        var tests = List.of(
            new Pair<>(makePoint(0.25, 0, 0.5), 0.25),
            new Pair<>(makePoint(0.25, 0, -0.25), 0.25),
            new Pair<>(makePoint(0.25, 0.5, -0.25), 0.25),
            new Pair<>(makePoint(1.25, 0, 0.5), 0.25),
            new Pair<>(makePoint(0.25, 0, -1.75), 0.25),
            new Pair<>(makePoint(1, 0, -1), 0.0),
            new Pair<>(makePoint(0, 0, 0), 0.0)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.planar.uSphereMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testVPlanarMap() {
        var tests = List.of(
            new Pair<>(makePoint(0.25, 0, 0.5), 0.5),
            new Pair<>(makePoint(0.25, 0, -0.25), 0.75),
            new Pair<>(makePoint(0.25, 0.5, -0.25), 0.75),
            new Pair<>(makePoint(1.25, 0, 0.5), 0.5),
            new Pair<>(makePoint(0.25, 0, -1.75), 0.25),
            new Pair<>(makePoint(1, 0, -1), 0.0),
            new Pair<>(makePoint(0, 0, 0), 0.0)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.planar.vSphereMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testUCylindricalMap() {
        var tests = List.of(
            new Pair<>(makePoint(0.0, 0.0, -1.0), 0.0),
            new Pair<>(makePoint(0.0, 0.5, -1.0), 0.0),
            new Pair<>(makePoint(0.0, 1.0, -1.0), 0.0),
            new Pair<>(makePoint(0.70711, 0.5, -0.70711), 0.125),
            new Pair<>(makePoint(1.0, 0.5, 0.0), 0.25),
            new Pair<>(makePoint(0.70711, 0.5, 0.70711), 0.375),
            new Pair<>(makePoint(0.0, -0.25, 1.0), 0.5),
            new Pair<>(makePoint(-0.70711, 0.5, 0.70711), 0.625),
            new Pair<>(makePoint(-1.0, 1.25, 0.0), 0.75),
            new Pair<>(makePoint(-0.70711, 0.5, -0.70711), 0.875)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.cylindircal.uSphereMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testVCylindricalMap() {
        var tests = List.of(
            new Pair<>(makePoint(0.0, 0.0, -1.0), 0.0),
            new Pair<>(makePoint(0.0, 0.5, -1.0), 0.5),
            new Pair<>(makePoint(0.0, 1.0, -1.0), 0.0),
            new Pair<>(makePoint(0.70711, 0.5, -0.70711), 0.5),
            new Pair<>(makePoint(1.0, 0.5, 0.0), 0.5),
            new Pair<>(makePoint(0.70711, 0.5, 0.70711), 0.5),
            new Pair<>(makePoint(0.0, -0.25, 1.0), 0.75),
            new Pair<>(makePoint(-0.70711, 0.5, 0.70711), 0.5),
            new Pair<>(makePoint(-1.0, 1.25, 0.0), 0.25),
            new Pair<>(makePoint(-0.70711, 0.5, -0.70711), 0.5)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.cylindircal.vSphereMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }
}
