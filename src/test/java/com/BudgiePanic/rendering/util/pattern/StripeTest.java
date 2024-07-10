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
package com.BudgiePanic.rendering.util.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.stripe;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Tests for the stripe material pattern
 */
public class StripeTest {
    
    @Test
    void testStripeYConstant() {
        var pattern = new BiPattern(stripe, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(makePoint(0, 0, 0)));
        assertEquals(Colors.white, pattern.colorAt(makePoint(0, 1, 0)));
        assertEquals(Colors.white, pattern.colorAt(makePoint(0, 2, 0)));
    }

    @Test
    void testStripeZConstant() {
        var pattern = new BiPattern(stripe, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(makePoint(0, 0, 0)));
        assertEquals(Colors.white, pattern.colorAt(makePoint(0, 0, 1)));
        assertEquals(Colors.white, pattern.colorAt(makePoint(0, 0, 2)));
    }

    @Test
    void testStripeXChanging() {
        var pattern = new BiPattern(stripe, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(makePoint(0f, 0f, 0f)));
        assertEquals(Colors.white, pattern.colorAt(makePoint(0.9f, 0f, 0f)));
        assertEquals(Colors.white, pattern.colorAt(makePoint(-1.1f, 0f, 0f)));
        assertEquals(Colors.black, pattern.colorAt(makePoint(1f, 0f, 0f)));
        assertEquals(Colors.black, pattern.colorAt(makePoint(-0.1f, 0f, 0f)));
        assertEquals(Colors.black, pattern.colorAt(makePoint(-1f, 0f, 0f)));
    }

    @Test
    void testStripeWithShapeTransform() {
        var shape = new Sphere(Transforms.identity().scale(2, 2, 2).assemble(), Material.pattern(new BiPattern(stripe, Colors.white, Colors.black)));
        var output = shape.material().pattern().colorAt(makePoint(1.5f, 0, 0), shape::toObjectSpace);
        assertEquals(Colors.white, output);
    }

    @Test 
    void testStripeWithLocalTransform() {
        var shape = new Sphere(
            Transforms.identity().assemble(), 
            Material.pattern(new BiPattern(
                stripe,
                Colors.white, 
                Colors.black,
                Transforms.identity().scale(2, 2, 2).assemble()
                )
            )
        );
        var output = shape.material().pattern().colorAt(makePoint(1.5f, 0, 0), shape::toObjectSpace);
        assertEquals(Colors.white, output);
    }

    @Test
    void testStripeWithShapeLocalTransform() {
        var shape = new Sphere(
            Transforms.identity().scale(2, 2, 2).assemble(),
            Material.pattern(
                new BiPattern(stripe, Colors.white, Colors.black, 
                Transforms.identity().translate(0.5f, 0, 0).assemble()
                )
            )
        );
        var output = shape.material().pattern().colorAt(makePoint(2.5f, 0, 0), shape::toObjectSpace);
        assertEquals(Colors.white, output);
    }
}
