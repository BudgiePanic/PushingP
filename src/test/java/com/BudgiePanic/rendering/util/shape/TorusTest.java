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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Tests for torus donut shape
 */
public class TorusTest {
    @Test
    void testLocalIntersect() {
        var shape = new Torus(Matrix4.identity(), Material.defaultMaterial(), 1f, 0.25f);
        var ray = new Ray(makePoint(-2, 0, 0), Directions.right);
        var result = shape.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(4, intersections.size());
        var _0 = intersections.get(0).a();
        var _1 = intersections.get(1).a();
        var _2 = intersections.get(2).a();
        var _3 = intersections.get(3).a();
        var expected0 = 0.75f;
        var expected1 = 1.25f;
        var expected2 = 2.75f;
        var expected3 = 3.25f;

        assertEquals(0, FloatHelp.compareFloat(expected0, _0), "expected: " + expected0 + " actual: " + _0);
        assertEquals(0, FloatHelp.compareFloat(expected1, _1), "expected: " + expected1 + " actual: " + _1);
        assertEquals(0, FloatHelp.compareFloat(expected2, _2), "expected: " + expected2 + " actual: " + _2);
        assertEquals(0, FloatHelp.compareFloat(expected3, _3), "expected: " + expected3 + " actual: " + _3);
    }

    @Test
    void testRayTorusIntersectionMiss() {
        var shape = new Torus(Matrix4.identity(), Material.defaultMaterial(), 1f, 0.25f);
        var ray = new Ray(makePoint(0, 0, -2), Directions.forward);
        var result = shape.intersect(ray); 
        assertTrue(result.isEmpty());    
    }

    @Test
    void testLocalNormal() {
        var radius = 1f;
        var thickness = 0.25f;
        var shape = new Torus(Matrix4.identity(), Material.defaultMaterial(), radius, thickness);
        var tests = List.of(
            new Pair<>(makePoint(radius + thickness, 0, 0), Directions.right),
            new Pair<>(makePoint(0, radius + thickness, 0), Directions.up),
            new Pair<>(makePoint(radius, 0, thickness), Directions.forward)
        );
        for (var test : tests) {
            var point = test.a();
            var expected = test.b();
            var actual = shape.normal(point);
            assertEquals(expected, actual);
        }
    }

    @Test
    void testTorusDivide() {
        var shape = new Torus(Matrix4.identity(), Material.defaultMaterial(), 1, 1);
        var result = shape.divide(0);
        assertEquals(shape, result);
    }

    @Test
    void testTorusAABB() {
        var shape = new Torus(Matrix4.identity(), Material.defaultMaterial(), 1, 0.25);
        var result = shape.bounds();
        var expected = new BoundingBox(makePoint(-1.25, -1.25, -0.25), makePoint(1.25, 1.25, 0.25));
        assertEquals(expected, result);
    }
}
