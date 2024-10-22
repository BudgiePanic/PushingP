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

import org.junit.jupiter.api.Test;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Tests for flat plane shape
 */
public class PlaneTest {
    
    @Test
    void testPlaneNormal() {
        final var expected = makeVector(0, 1, 0);
        var plane = new Plane(Matrix4.identity());
        List.of(
            makePoint(), makePoint(10, 0, -10), makePoint(-5, 0, 150)
        ).stream().map(plane::localNormal).forEach(result -> assertEquals(expected, result));
    }

    @Test
    void testPlaneIntersectionParallel() {
        // A ray that is parallel to the plane misses it and has no intersection
        var plane = new Plane(Matrix4.identity());
        var ray = new Ray(makePoint(0,10,0), makeVector(0, 0, 1));
        var intersections = plane.localIntersect(ray);
        assertTrue(intersections.isEmpty());
    }

    @Test
    void testPlaneIntersectionCoplanar() {
        // A coplanar ray intersects down the plane, resulting in infinite intersections
        // However, because the plane is infinitly thin, we would never see these intersections
        // so the correct behaviour of the plane is to return no hits
        var plane = new Plane(Matrix4.identity());
        var ray = new Ray(makePoint(0,0,0), makeVector(0, 0, 1));
        var intersections = plane.localIntersect(ray);
        assertTrue(intersections.isEmpty());
    }

    @Test
    void testPlaneIntersectionAbove() {
        var plane = new Plane(Matrix4.identity());
        var ray = new Ray(makePoint(0,1,0), makeVector(0, -1, 0));
        var intersections = plane.localIntersect(ray);
        assertTrue(intersections.isPresent());
        assertEquals(1, intersections.get().size());
        assertEquals(plane, intersections.get().get(0).shape());
        assertEquals(1f, intersections.get().get(0).a());
    }

    @Test 
    void testPlaneIntersectionBelow() {
        var plane = new Plane(Matrix4.identity());
        var ray = new Ray(makePoint(0, -1,0), makeVector(0, 1, 0));
        var intersections = plane.localIntersect(ray);
        assertTrue(intersections.isPresent());
        assertEquals(1, intersections.get().size());
        assertEquals(plane, intersections.get().get(0).shape());
        assertEquals(1f, intersections.get().get(0).a());
    }

    @Test
    void testCylinderSolid() {
        var plane = new Plane(Matrix4.identity());
        assertFalse(plane.isSolid());
    }

    @Test
    void testCylinderDivide() {
        var shape = new Plane(Matrix4.identity());
        var result = shape.divide(0);
        assertEquals(shape, result);
    } 
}
