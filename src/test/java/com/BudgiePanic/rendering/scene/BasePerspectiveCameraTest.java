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
package com.BudgiePanic.rendering.scene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.View;

public class BasePerspectiveCameraTest {

    protected static final class TestCamera extends BasePerspectiveCamera {

        public TestCamera(int width, int height, double fov, double focalDistance, Matrix4 transform) { super(width, height, fov, focalDistance, transform); }
        @Override
        public Ray createRay(double pixelColumn, double pixelRow, double time) { return null; }
        @Override
        public Color pixelAt(World world, double pixelColumn, double pixelRow, double time) { return null;}
    }

    @Test
    void testProject() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(0, 0, 1), makePoint(), Directions.up));
        var resultA = camera.project(camera.transform(makePoint(-1, 0, 0)));
        var resultB =  camera.project(camera.transform(makePoint(1, 0, 0)));
        var expectedA = new int[] {0,5};
        var expectedB = new int[] {10 ,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectBehind() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(0, 0, -1), makePoint(), Directions.up));
        var resultA = camera.project(camera.transform(makePoint(-1, 0, 0)));
        var resultB =  camera.project(camera.transform(makePoint(1, 0, 0)));
        // A quirk of our camera transform is that when the transform 'makes' the camera looks in the +ve z direction
        // it effectively reflects transformed points about the 'x' and 'z' axis, the knock on effect is our screen coords will be flipped
        // ultimately this doesn't matter because a line from A to B will look the exact same as a line from B to A
        var expectedA = new int[] {10 ,5};
        var expectedB = new int[] {0,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectA() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var resultA = camera.project(camera.transform(makePoint(-1, 0, -1)));
        var resultB =  camera.project(camera.transform(makePoint(1, 0, -1)));
        var expectedA = new int[] {0,5};
        var expectedB = new int[] {10 ,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectB() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,1), Directions.up));
        var resultA = camera.project(camera.transform(makePoint(-1, 0, 1)));
        var resultB =  camera.project(camera.transform(makePoint(1, 0, 1)));
        var expectedA = new int[] {10 ,5};
        var expectedB = new int[] {0,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectC() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var resultA = camera.project(camera.transform(makePoint(-1, 0, -1.1)));
        var resultB =  camera.project(camera.transform(makePoint(1, 0, -1.1)));
        var expectedA = new int[] {0,5};
        var expectedB = new int[] {10 ,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testClippingPlaneDistance() {
        // create various clipping planes and points, verify the point distance to plane makes sense
        var plane = new BasePerspectiveCamera.ClippingPlane(makePoint(), Directions.up);

        var distance = plane.distanceTo(makePoint(0, 1, 0));
        assertTrue(FloatHelp.compareFloat(1, distance) == 0);

        distance = plane.distanceTo(makePoint(0, -1, 0));
        assertTrue(FloatHelp.compareFloat(-1, distance) == 0);

        distance = plane.distanceTo(makePoint(0, -3.5, 0));
        assertTrue(FloatHelp.compareFloat(-3.5, distance) == 0);

        distance = plane.distanceTo(makePoint(0, 3.5, 0));
        assertTrue(FloatHelp.compareFloat(3.5, distance) == 0);
    }

    @Test
    void testClippingPlaneDistanceA() {
        // create various clipping planes and points, verify the point distance to plane makes sense
        var plane = new BasePerspectiveCamera.ClippingPlane(makePoint(), Directions.left);

        var distance = plane.distanceTo(makePoint(0, 1, 0));
        assertTrue(FloatHelp.compareFloat(0, distance) == 0);

        distance = plane.distanceTo(makePoint(1, 0, 0));
        assertTrue(FloatHelp.compareFloat(-1, distance) == 0);

        distance = plane.distanceTo(makePoint(-1, 0, 0));
        assertTrue(FloatHelp.compareFloat(1, distance) == 0);
    }

    @Test
    void testClipPlaneGeneration() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        // expected vectors are taken from the book (which also assumes FOV = 90) but are inverted because their camera faces +ve z and ours faces -ve z
        var expectedLeft = makeVector(-1.0/Math.sqrt(2), 0, -1.0/Math.sqrt(2));
        var expectedRight = makeVector(1.0/Math.sqrt(2.0), 0, -1.0/Math.sqrt(2.0));
        var expectedTop = makeVector(0, -1.0/Math.sqrt(2.0), -1.0/Math.sqrt(2.0));
        var expectedBottom = makeVector(0, 1.0/Math.sqrt(2.0), -1.0/Math.sqrt(2.0));
        var expectedNear = makeVector(0, 0, -1);
        assertEquals(expectedLeft, camera.left.normal());
        assertEquals(expectedRight, camera.right.normal());
        assertEquals(expectedTop, camera.top.normal());
        assertEquals(expectedBottom, camera.bottom.normal());
        assertEquals(expectedNear, camera.near.normal());
    }

    @Test
    void testClipPlaneGenerationA() {
        // camera with FOV != 90 degrees
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(60), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var expectedLeft = makeVector(-Math.cos(AngleHelp.toRadians(30)), 0, -Math.sin(AngleHelp.toRadians(30)));
        var expectedRight = makeVector(Math.cos(AngleHelp.toRadians(30)), 0, -Math.sin(AngleHelp.toRadians(30)));
        var expectedTop = makeVector(0, -Math.cos(AngleHelp.toRadians(30)), -Math.sin(AngleHelp.toRadians(30)));
        var expectedBottom = makeVector(0, Math.cos(AngleHelp.toRadians(30)), -Math.sin(AngleHelp.toRadians(30)));
        var expectedNear = makeVector(0, 0, -1);
        assertEquals(expectedLeft, camera.left.normal());
        assertEquals(expectedRight, camera.right.normal());
        assertEquals(expectedTop, camera.top.normal());
        assertEquals(expectedBottom, camera.bottom.normal());
        assertEquals(expectedNear, camera.near.normal());
    }

    @Test
    void testNearClip() {
        var camera = new TestCamera(10, 10, AngleHelp.toRadians(90), 1.0, View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var line = new Tuple[] {makePoint(0, 0, -1.5), makePoint(0, 0, 1)};
        var clippingPlane = camera.near;
        assertTrue(clippingPlane.pointInside(line[0]));
        assertFalse(clippingPlane.pointInside(line[1]));
        var result = clippingPlane.clip(line[0], line[1]);
        assertNotEquals(line[0], result);
        assertEquals(makePoint(0,0,-1.0), result);
    }

    @Test
    void testNearClipA() {
        var camera = new TestCamera(10, 10, AngleHelp.toRadians(90), 1.0, View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var line = new Tuple[] {makePoint(0, 1, -1.5), makePoint(0, 1, 1)};
        var clippingPlane = camera.near;
        assertTrue(clippingPlane.pointInside(line[0]));
        assertFalse(clippingPlane.pointInside(line[1]));
        var result = clippingPlane.clip(line[0], line[1]);
        assertNotEquals(line[0], result);
        assertEquals(makePoint(0,1,-1.0), result);
    }

    @Test 
    void testClipping() {
        var camera = new TestCamera(10, 10, AngleHelp.toRadians(90), 1.0, View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        List<Pair<Pair<Tuple, Tuple>, Optional<Pair<Tuple, Tuple>>>> tests = List.of(
            new Pair<>(new Pair<>(makePoint(-1, 0, 1), makePoint(1, 0, 1)), Optional.empty()), // outside near
            new Pair<>(new Pair<>(makePoint(0, 0, -2), makePoint(-0.3, 0.5, -2.5)), Optional.of(new Pair<>(makePoint(0,0,-2), makePoint(-0.3, 0.5, -2.5)))), // all inside, no clip needed
            new Pair<>(new Pair<>(makePoint(-10, 0, -2), makePoint(10, 0, -2)), Optional.of(new Pair<>(makePoint(-2,0,-2), makePoint(2,0,-2)))), // clipped A
            new Pair<>(new Pair<>(makePoint(0, -10, -2), makePoint(0, 10, -2)), Optional.of(new Pair<>(makePoint(0,-2,-2), makePoint(0,2,-2))))  // clipped B
        );
        for (final var test : tests) {
            var line = new Tuple[] {test.a().a(), test.a().b()};
            var expected = test.b();
            var result = camera.clip(line[0], line[1]);
            assertEquals(expected, result);
        }
    }

    @Test
    void testLeftClip() {
        var camera = new TestCamera(10, 10, AngleHelp.toRadians(90), 1.0, View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var line = new Pair<>(makePoint(0, 0, 1), makePoint(1, 0, 0));
        assertFalse(camera.left.pointInside(line.a()));
        assertFalse(camera.left.pointInside(line.b()));
    }

    @Test
    void testRightClip() {
        var camera = new TestCamera(10, 10, AngleHelp.toRadians(90), 1.0, View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var line = new Pair<>(makePoint(0, 0, 1), makePoint(-1, 0, 0));
        assertFalse(camera.right.pointInside(line.a()));
        assertFalse(camera.right.pointInside(line.b()));
    }

    @Test
    void testTopClip() {
        var camera = new TestCamera(10, 10, AngleHelp.toRadians(90), 1.0, View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var line = new Pair<>(makePoint(0, 0, 1), makePoint(0, 1, 0));
        assertFalse(camera.top.pointInside(line.a()));
        assertFalse(camera.top.pointInside(line.b()));
    }

    @Test
    void testBottomClip() {
        var camera = new TestCamera(10, 10, AngleHelp.toRadians(90), 1.0, View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var line = new Pair<>(makePoint(0, 0, 1), makePoint(0, -1, 0));
        assertFalse(camera.bottom.pointInside(line.a()));
        assertFalse(camera.bottom.pointInside(line.b()));
    }
}
