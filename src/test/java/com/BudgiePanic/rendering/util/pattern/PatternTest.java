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

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Tests for pattern coordinate space transformations.
 */
public class PatternTest {

    public static class TestPattern implements Pattern {
        Matrix4 transform;
        { transform = Matrix4.identity(); }
        @Override
        public Color colorAt(Tuple point) { return new Color(point.x, point.y, point.z); }
        @Override
        public Matrix4 transform() { return transform; }
    }

    @Test
    void testPatternObjectTransform() {
        var shape = new Sphere(Transforms.identity().scale(2, 2, 2).assemble());
        var pattern = new TestPattern();
        assertEquals(new Color(1, 1.5f, 2), pattern.colorAt(Tuple.makePoint(2, 3, 4), shape::toObjectSpace));
    }

    @Test
    void testPatternTransform() {
        var shape = Sphere.defaultSphere();
        var pattern = new TestPattern();
        pattern.transform = Transforms.identity().scale(2, 2, 2).assemble();
        var result = pattern.colorAt(Tuple.makePoint(2, 3, 4), shape::toObjectSpace);
        assertEquals(new Color(1, 1.5f, 2), result);
    }

    @Test
    void testPatternTransformObjectTransform() {
        var shape = new Sphere(Transforms.identity().scale(2, 2, 2).assemble());
        var pattern = new TestPattern();
        pattern.transform = Transforms.identity().translate(0.5f, 1, 1.5f).assemble();
        var output = pattern.colorAt(Tuple.makePoint(2.5f,3,3.5f), shape::toObjectSpace);
        assertEquals(new Color(0.75f, 0.5f, 0.25f), output);
    }
}
