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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Unit tests for the ray class.
 * 
 * @author BudgiePanic
 */
public class RayTest {
    
    @Test
    void testRayConstructor() {
        var origin = Tuple.makePoint(1,2,3);
        var direction = Tuple.makeVector(4, 5, 6);
        var ray = new Ray(origin, direction);

        var expectedOrigin = Tuple.makePoint(1,2,3);
        var expectedDirection = Tuple.makeVector(4, 5, 6);
        assertEquals(expectedDirection, ray.direction());
        assertEquals(expectedOrigin, ray.origin());
    }

    @Test
    void testRayPosition() {
        var ray = new Ray(Tuple.makePoint(2, 3, 4), Tuple.makeVector(1, 0, 0));

        assertEquals(Tuple.makePoint(2, 3, 4), ray.position(0f));
        assertEquals(Tuple.makePoint(3, 3, 4), ray.position(1f));
        assertEquals(Tuple.makePoint(1, 3, 4), ray.position(-1f));
        assertEquals(Tuple.makePoint(4.5f, 3, 4), ray.position(2.5f));
    }

    @Test
    void testRayTransform() {
        var ray = new Ray(Tuple.makePoint(1, 2, 3), Tuple.makeVector(0, 1, 0));
        var transform = Transforms.identity().translate(3, 4, 5).assemble();
        // The book wants us to test for immutability here, but Tuples and Rays are already immutable by design :)
        var result = ray.transform(transform);
        var expected = new Ray(Tuple.makePoint(4, 6, 8), Tuple.makeVector(0, 1, 0));
        assertEquals(expected, result);
    }

    @Test
    void testRayTransformA() {
        var ray = new Ray(Tuple.makePoint(1, 2, 3), Tuple.makeVector(0, 1, 0));
        var transform = Transforms.identity().scale(2, 3, 4).assemble();
        var result = ray.transform(transform);
        var expected = new Ray(Tuple.makePoint(2, 6, 12), Tuple.makeVector(0, 3, 0));
        assertEquals(expected, result);
    }

}
