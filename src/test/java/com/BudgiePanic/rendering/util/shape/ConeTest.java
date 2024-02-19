package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;

public class ConeTest {
    @Test
    void testLocalIntersect() {
        var shape = new Cone(Matrix4.identity());
        List<Pair<Ray, Pair<Float, Float>>> tests = List.of(
            new Pair<>(new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1).normalize()), new Pair<>(5f,5f)),
            new Pair<>(new Ray(makePoint(0, 0, -5), makeVector(1,1,1).normalize()), new Pair<>(8.66025f,8.66025f)),
            new Pair<>(new Ray(makePoint(1, 1, -5), makeVector(-0.5f, -1, 1).normalize()), new Pair<>(4.55006f,49.44994f))
        );
        for (var test : tests) {
            var result = shape.localIntersect(test.a());
            assertTrue(result.isPresent());
            var intersections = result.get();
            assertEquals(2, intersections.size());
            var expectedA = test.b().a();
            var expectedB = test.b().b();
            var actualA = intersections.get(0).a();
            var actualB = intersections.get(1).a();
            assertTrue(FloatHelp.compareFloat(expectedA, actualA) == 0, Float.toString(expectedA - actualA));
            assertTrue(FloatHelp.compareFloat(expectedB, actualB) == 0, Float.toString(expectedB - actualB));
        }
    }

    @Test
    void testLocalIntersectParrallelRay() {
        var shape = new Cone(Matrix4.identity());
        var ray = new Ray(makePoint(0,0,-1), makeVector(0, 1, 1).normalize());
        var result = shape.localIntersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(1, intersections.size());
        var expected = 0.35355f;
        var actual = intersections.getFirst().a();
        assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "act - exp " + (expected - actual));
    }

    @Test
    void testLocalIntersectCaps() {
        var shape = new Cone(Matrix4.identity(), 0.5f, -0.5f, true);
        List<Pair<Ray, Integer>> tests = List.of(
            new Pair<>(new Ray(makePoint(0, 0, -5), makeVector(0, 1, 0).normalize()), 0),
            new Pair<>(new Ray(makePoint(0, 0, -0.25f), makeVector(0, 1, 1).normalize()), 2),
            new Pair<>(new Ray(makePoint(0, 0, -0.25f), makeVector(0, 1, 0).normalize()), 4)
        );
        for (var test: tests) {
            var result = shape.localIntersect(test.a());
            var expected = test.b();
            if (expected == 0) {
                assertTrue(result.isEmpty());
            } else {
                var intersections = result.get();
                assertEquals(expected, intersections.size());
            }
        }
    }

    @Test
    void testLocalNormal() {
        var shape = new Cone(Matrix4.identity());
        final float sqrt2 = (float) Math.sqrt(2);
        List<Pair<Tuple, Tuple>> tests = List.of(
            new Pair<>(makePoint(0, 0, 0), makeVector(0, 0, 0)),
            new Pair<>(makePoint(1, 1, 1), makeVector(1, -sqrt2, 1)),
            new Pair<>(makePoint(-1, -1, 0), makeVector(-1, 1, 0))
        );
        for(var test : tests) {
            var result = shape.localNormal(test.a());
            var expected = test.b();
            assertEquals(expected, result);
        }
    }
}
