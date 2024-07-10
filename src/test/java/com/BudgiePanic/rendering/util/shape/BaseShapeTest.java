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
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Tests for common shape functionality. Shape coordinate space transform tests.
 */
public class BaseShapeTest {
    
    public static class TestShape extends BaseShape {

        public Ray localIntersectRayResult;

        public TestShape(Matrix4 transform) {
            super(transform);
        }

        @Override
        protected Optional<List<Intersection>> localIntersect(Ray ray) {
            // This will allow us to check that BaseShape is correctly transforming the ray to object space
            // before calling local intersect
            this.localIntersectRayResult = ray;
            return Optional.empty();
        }

        @Override
        protected Tuple localNormal(Tuple point) {
            // this will allow us to check that BaseShape is correctly transforming the point before
            // passing it to localNormal
            return Tuple.makeVector(point.x, point.y, point.z); 
        }
        @Override
        public BoundingBox bounds() { return new BoundingBox(makePoint(-1, -1, -1), makePoint(1, 1, 1)); }
        @Override
        public boolean isSolid() { return true; }
    }

    @Test
    void testScaledShapeRayIntersection() {
        // Replaces SphereTest::testSphereRayTransform
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var shape = new TestShape(Transforms.identity().scale(2, 2, 2).assemble());
        @SuppressWarnings(value = { "unused" }) 
        var intersections = shape.intersect(ray); // force the ray to pass through BaseShape's methods.
        assertEquals(Tuple.makePoint(0, 0, -2.5f), shape.localIntersectRayResult.origin());
        assertEquals(Tuple.makeVector(0, 0, 0.5f), shape.localIntersectRayResult.direction());
    }

    @Test
    void testTranslatedShapeRayIntersection() {
        // Replaces SphereTest::testSphereRayTransformA
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var shape = new TestShape(Transforms.identity().translate(5, 0, 0).assemble());
        @SuppressWarnings(value = { "unused" }) 
        var intersections = shape.intersect(ray); // force the ray to pass through BaseShape's methods.
        assertEquals(Tuple.makePoint(-5, 0, -5), shape.localIntersectRayResult.origin());
        assertEquals(Tuple.makeVector(0, 0, 1), shape.localIntersectRayResult.direction());
    }

    @Test
    void testTranslatedShapeNormalTransform() {
        // Replaces SphereTest::testNormalNonOrigin
        var shape = new TestShape(Transforms.identity().translate(0, 1, 0).assemble());
        var result = shape.normal(Tuple.makePoint(0, 1.70711f, -0.70711f));
        assertEquals(Tuple.makeVector(0, 0.70711f, -0.70711f), result);
    }

    @Test
    void testTransformedShapeNormal() {
        // Replaces SphereTest::testNormalTransformedOrigin
        final float piOverFive = (float) (Math.PI / 5.0);
        final float sqrtTwoOverTwo = (float) (Math.sqrt(2.0) / 2.0);
        var shape = new TestShape(Transforms.identity().rotateZ(piOverFive).scale(1, 0.5f, 1).assemble());
        var result = shape.normal(Tuple.makePoint(0, sqrtTwoOverTwo, -sqrtTwoOverTwo));
        assertEquals(Tuple.makeVector(0, 0.97014f, -0.24254f), result);
    }
   
    @Test
    void testParentAttribute() {
        var shape = new TestShape(Transforms.identity().assemble());
        assertTrue(shape.parent().isEmpty());
    }

    @Test
    void testPointTransform() {
        var shape = new TestShape(Transforms.identity().assemble());
        var result = shape.pointToWorldSpace(Tuple.makePoint(1, 0, 0));
        assertEquals(Tuple.makePoint(1, 0, 0), result);
    }

    @Test
    void testPointTransformA() {
        var shape = new TestShape(Transforms.identity().translate(1, 1, 0).assemble());
        var result = shape.pointToWorldSpace(Tuple.makePoint(1, 0, 0));
        assertEquals(Tuple.makePoint(2, 1, 0), result);
    }

    @Test
    void testPointTransformB() {
        var shape = new TestShape(Transforms.identity().scale(0.5).assemble());
        var result = shape.pointToWorldSpace(Tuple.makePoint(1, 0, 0));
        assertEquals(Tuple.makePoint(0.5, 0, 0), result);
    }

    @Test
    void testPointTransformC() {
        var group = new Group(Transforms.identity().scale(0.5).assemble());
        var shape = new TestShape(Transforms.identity().assemble());
        group.addShape(shape);
        var result = shape.pointToWorldSpace(Tuple.makePoint(1, 0, 0));
        assertEquals(Tuple.makePoint(0.5, 0, 0), result);
    }

}
