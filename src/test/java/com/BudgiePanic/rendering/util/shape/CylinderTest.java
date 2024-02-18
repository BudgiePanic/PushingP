package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

public class CylinderTest {
    @Test
    void testLocalIntersectA() {
        // a miss
        var rays = List.of(
            new Ray(makePoint(1, 0, 0), makeVector(0, 1, 0).normalize()),
            new Ray(makePoint(0, 0, 0), makeVector(0, 1, 0).normalize()),
            new Ray(makePoint(0, 0, -5), makeVector(1, 1, 1).normalize())
        );
        var shape = new Cylinder(Matrix4.identity());
        for (var ray : rays) {
            var result = shape.localIntersect(ray);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testLocalIntersectB() {
        // hits
        List<Pair<Ray, Pair<Float, Float>>> tests = List.of(
            new Pair<>(new Ray(makePoint(1, 0, -5), makeVector(0,0,1).normalize()), new Pair<>(5f, 5f)),
            new Pair<>(new Ray(makePoint(0, 0, -5), makeVector(0,0,1).normalize()), new Pair<>(4f, 6f)),
            new Pair<>(new Ray(makePoint(0.5f, 0, -5), makeVector(0.1f,1,1).normalize()), new Pair<>(6.80798f, 7.08872f))
        );
        var shape = new Cylinder(Matrix4.identity());
        for (var test : tests) {
            var result = shape.localIntersect(test.a());
            assertTrue(result.isPresent());
            var intersections = result.get();
            assertEquals(2, intersections.size());
            var expectedA = test.b().a();
            var actualA = intersections.get(0).a();
            assertTrue(FloatHelp.compareFloat(expectedA, expectedA) == 0, expectedA + " " + actualA + (expectedA - actualA));
            var expectedB = test.b().b();
            var actualB = intersections.get(1).a();
            assertTrue(FloatHelp.compareFloat(expectedB, expectedB) == 0, expectedB + " " + actualB + (expectedB - actualB));
        }
    }

    @Test
    void testLocalNormal() {
        List<Pair<Tuple, Tuple>> tests = List.of(
            // point input | expected result
            new Pair<>(makePoint(1, 0, 0), makeVector(1, 0, 0)),
            new Pair<>(makePoint(0, 5, -1), makeVector(0, 0, -1)),
            new Pair<>(makePoint(0, -2, 1), makeVector(0, 0, 1)),
            new Pair<>(makePoint(-1, 1, 0), makeVector(-1, 0, 0))
        );
        var shape = new Cylinder(Matrix4.identity());
        for (var test: tests) {
            var result = shape.localNormal(test.a());
            var expected = test.b();
            assertEquals(expected, result, test.toString());
        }
    }

    @Test
    void testCylinderDefaultBounds() {
        Cylinder shape = new Cylinder(Matrix4.identity());
        assertEquals(Float.POSITIVE_INFINITY, shape.maximum);
        assertEquals(Float.NEGATIVE_INFINITY, shape.minimum);
        shape = new Cylinder(Matrix4.identity(), Material.defaultMaterial());
        assertEquals(Float.POSITIVE_INFINITY, shape.maximum);
        assertEquals(Float.NEGATIVE_INFINITY, shape.minimum);
    }

    @Test
    void testCylinderTruncation() {
        var shape = new Cylinder(Matrix4.identity(), 2, 1);
        List<Pair<Ray, Integer>> tests = List.of(
            new Pair<>(new Ray(makePoint(0, 1.5f, 0), makeVector(0.1f, 1, 0).normalize()), 0),
            new Pair<>(new Ray(makePoint(0, 3, -5), makeVector(0, 0, 1).normalize()), 0),
            new Pair<>(new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1).normalize()), 0),
            new Pair<>(new Ray(makePoint(0, 2, -5), makeVector(0, 0, 1).normalize()), 0),
            new Pair<>(new Ray(makePoint(0, 1, -5), makeVector(0, 0, 1).normalize()), 0),
            new Pair<>(new Ray(makePoint(0, 1.5f, -2), makeVector(0, 0, 1).normalize()), 2)
        );
        for(var test : tests) {
            var result = shape.localIntersect(test.a());
            if (test.b() == 0) {
                assertTrue(result.isEmpty());
            } else {
                var intersections = result.get();
                assertEquals(2, intersections.size());
            }
        }
    }
}
