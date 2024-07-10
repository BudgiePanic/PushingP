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
package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Tests for cylinder shape
 */
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

    @Test
    void testCylinderCapDefaultProperties() {
        Cylinder shape = new Cylinder(Matrix4.identity());
        assertFalse(shape.closed);
        shape = new Cylinder(Matrix4.identity(), Material.defaultMaterial());
        assertFalse(shape.closed);
        shape = new Cylinder(Matrix4.identity(), Material.defaultMaterial(),0,0);
        assertFalse(shape.closed);
        shape = new Cylinder(Matrix4.identity(),0,0);
        assertFalse(shape.closed);
    }

    @Test
    void testCylinderCapIntersection() {
        var shape = new Cylinder(Matrix4.identity(), 2, 1, true);
        List<Pair<Ray, Integer>> tests = List.of(
            new Pair<>(new Ray(makePoint(0, 3, 0), makeVector(0, -1, 0).normalize()), 2),
            new Pair<>(new Ray(makePoint(0, 3, -2), makeVector(0, -1, 2).normalize()), 2),
            new Pair<>(new Ray(makePoint(0, 4, -2), makeVector(0, -1, 1).normalize()), 2),
            new Pair<>(new Ray(makePoint(0, 0, -2), makeVector(0, 1, 2).normalize()), 2),
            new Pair<>(new Ray(makePoint(0, -1, -2), makeVector(0, 1, 1).normalize()), 2)
        );
        for(var test : tests) {
            var result = shape.localIntersect(test.a());
            assertTrue(result.isPresent());
            var intersections = result.get();
            assertEquals(2, intersections.size());
        }
    }

    @Test
    void testCylinderCapNormals() {
        var shape = new Cylinder(Matrix4.identity(), 2, 1, true);
        List<Pair<Tuple, Tuple>> tests = List.of(
            new Pair<>(makePoint(0, 1, 0), makeVector(0,-1,0)),
            new Pair<>(makePoint(0.5f, 1, 0), makeVector(0,-1,0)),
            new Pair<>(makePoint(0, 1, 0.5f), makeVector(0,-1,0)),
            new Pair<>(makePoint(0, 2, 0), makeVector(0,1,0)),
            new Pair<>(makePoint(0.5f, 2, 0), makeVector(0,1,0)),
            new Pair<>(makePoint(0, 2, 0.5f), makeVector(0,1,0))
        );
        for (var test : tests) {
            var result = shape.localNormal(test.a());
            var expected = test.b();
            assertEquals(expected, result);
        }
    }

    @Test
    void testCylinderSolid() {
        var cylinder = new Cylinder(Matrix4.identity(),1,0,false);
        assertFalse(cylinder.isSolid());
        cylinder = new Cylinder(Matrix4.identity(), 1, 0, true);
        assertTrue(cylinder.isSolid());
    }

    @Test
    void testCylinderDivide() {
        var shape = new Cylinder(Matrix4.identity());
        var result = shape.divide(0);
        assertEquals(shape, result);
    }
}
