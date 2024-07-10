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
package com.BudgiePanic.rendering.util.intersect;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.SmoothTriangle;
import com.BudgiePanic.rendering.util.shape.Sphere;

/**
 * Tests if ray-shape intersection meta data is calculated correctly
 */
public class ShadingInfoTest {
    
    @Test
    void testIntersectionPrecompute() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var shape = Sphere.defaultSphere();
        var intersection = new Intersection(4.0, shape);
        var result = intersection.computeShadingInfo(ray);

        assertEquals(intersection.a(), result.a());
        assertEquals(intersection.shape(), result.shape());
        assertEquals(Tuple.makePoint(0, 0, -1), result.point());
        assertEquals(Tuple.makeVector(0, 0, -1), result.eyeVector());
        assertEquals(Tuple.makeVector(0, 0, -1), result.normalVector());
    }

    @Test
    void testIntersectionOutsideShape() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var shape = Sphere.defaultSphere();
        var intersection = new Intersection(4.0, shape);
        var result = intersection.computeShadingInfo(ray);
        assertFalse(result.intersectInside());
    }

    @Test
    void testIntersectionInsideShape() {
        var ray = new Ray(Tuple.makePoint(), Tuple.makeVector(0, 0, 1));
        var shape = Sphere.defaultSphere();
        var intersection = new Intersection(1.0, shape);
        var result = intersection.computeShadingInfo(ray);
        assertTrue(result.intersectInside());
        assertEquals(Tuple.makePoint(0, 0, 1), result.point());
        assertEquals(Tuple.makeVector(0, 0, -1), result.eyeVector());
        assertEquals(Tuple.makeVector(0, 0, -1), result.normalVector());
    }

    @Test
    void testIntersectionComputeSmoothTriangle() {
        var testTriangle = new SmoothTriangle(
        makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0),
        makeVector(0, 1, 0), makeVector(-1, 0, 0), makeVector(1, 0, 0));
        var intersection = new Intersection(1.0, testTriangle, new Pair<Double, Double>(0.45, 0.25));
        var ray = new Ray(makePoint(-0.2f, 0.3f, -2), makeVector(0, 0, 1));
        var info = intersection.computeShadingInfo(ray);
        var expected = makeVector(-0.5547f, 0.83205f, 0);
        assertEquals(expected, info.normalVector());
    }
}
