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

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;

/**
 * Tests for converting 3D points on shape to 2D points on shape surface.
 */
public class CoordinateMapperTest {

    @Test
    void testUSphereMap() {
        var tests = List.of(
            new Pair<>(makePoint(0, 0, -1), 0.0),
            new Pair<>(makePoint(1, 0, 0), 0.25),
            new Pair<>(makePoint(0, 0, 1), 0.5),
            new Pair<>(makePoint(-1, 0, 0), 0.75),
            new Pair<>(makePoint(0, 1, 0), 0.5),
            new Pair<>(makePoint(0, -1, 0), 0.5),
            new Pair<>(makePoint((Math.sqrt(2) / 2), (Math.sqrt(2) / 2), 0), 0.25)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.sphere.uMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testVSphereMap() {
        var tests = List.of(
            new Pair<>(makePoint(0, 0, -1), 0.5),
            new Pair<>(makePoint(1, 0, 0), 0.5),
            new Pair<>(makePoint(0, 0, 1), 0.5),
            new Pair<>(makePoint(-1, 0, 0), 0.5),
            new Pair<>(makePoint(0, 1, 0), 1.0),
            new Pair<>(makePoint(0, -1, 0), 0.0),
            new Pair<>(makePoint((Math.sqrt(2) / 2), (Math.sqrt(2) / 2), 0), 0.75)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.sphere.vMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testUPlanarMap() {
        var tests = List.of(
            new Pair<>(makePoint(0.25, 0, 0.5), 0.25),
            new Pair<>(makePoint(0.25, 0, -0.25), 0.25),
            new Pair<>(makePoint(0.25, 0.5, -0.25), 0.25),
            new Pair<>(makePoint(1.25, 0, 0.5), 0.25),
            new Pair<>(makePoint(0.25, 0, -1.75), 0.25),
            new Pair<>(makePoint(1, 0, -1), 0.0),
            new Pair<>(makePoint(0, 0, 0), 0.0)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.planar.uMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testVPlanarMap() {
        var tests = List.of(
            new Pair<>(makePoint(0.25, 0, 0.5), 0.5),
            new Pair<>(makePoint(0.25, 0, -0.25), 0.75),
            new Pair<>(makePoint(0.25, 0.5, -0.25), 0.75),
            new Pair<>(makePoint(1.25, 0, 0.5), 0.5),
            new Pair<>(makePoint(0.25, 0, -1.75), 0.25),
            new Pair<>(makePoint(1, 0, -1), 0.0),
            new Pair<>(makePoint(0, 0, 0), 0.0)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.planar.vMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testUCylindricalMap() {
        var tests = List.of(
            new Pair<>(makePoint(0.0, 0.0, -1.0), 0.0),
            new Pair<>(makePoint(0.0, 0.5, -1.0), 0.0),
            new Pair<>(makePoint(0.0, 1.0, -1.0), 0.0),
            new Pair<>(makePoint(0.70711, 0.5, -0.70711), 0.125),
            new Pair<>(makePoint(1.0, 0.5, 0.0), 0.25),
            new Pair<>(makePoint(0.70711, 0.5, 0.70711), 0.375),
            new Pair<>(makePoint(0.0, -0.25, 1.0), 0.5),
            new Pair<>(makePoint(-0.70711, 0.5, 0.70711), 0.625),
            new Pair<>(makePoint(-1.0, 1.25, 0.0), 0.75),
            new Pair<>(makePoint(-0.70711, 0.5, -0.70711), 0.875)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.cylindircal.uMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testVCylindricalMap() {
        var tests = List.of(
            new Pair<>(makePoint(0.0, 0.0, -1.0), 0.0),
            new Pair<>(makePoint(0.0, 0.5, -1.0), 0.5),
            new Pair<>(makePoint(0.0, 1.0, -1.0), 0.0),
            new Pair<>(makePoint(0.70711, 0.5, -0.70711), 0.5),
            new Pair<>(makePoint(1.0, 0.5, 0.0), 0.5),
            new Pair<>(makePoint(0.70711, 0.5, 0.70711), 0.5),
            new Pair<>(makePoint(0.0, -0.25, 1.0), 0.75),
            new Pair<>(makePoint(-0.70711, 0.5, 0.70711), 0.5),
            new Pair<>(makePoint(-1.0, 1.25, 0.0), 0.25),
            new Pair<>(makePoint(-0.70711, 0.5, -0.70711), 0.5)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.cylindircal.vMap(test.a());
            assertTrue(FloatHelp.compareFloat(expected, actual) == 0, "expected " + expected + " actual " + actual);
        }
    }

    @Test
    void testCubeFaceMapping() {
        var tests = List.of(
            new Pair<>(makePoint(-1, 0.5, -0.25), CoordinateMapper.Cube.Face.Left),
            new Pair<>(makePoint(1.1, -0.75, 0.8), CoordinateMapper.Cube.Face.Right),
            new Pair<>(makePoint(0.1, 0.6, 0.9), CoordinateMapper.Cube.Face.Front),
            new Pair<>(makePoint(-0.7, 0.0, -2), CoordinateMapper.Cube.Face.Back),
            new Pair<>(makePoint(0.5, 1, 0.9), CoordinateMapper.Cube.Face.Up),
            new Pair<>(makePoint(-0.2, -1.3, 1.1), CoordinateMapper.Cube.Face.Down)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = CoordinateMapper.Cube.getFace(test.a());
            assertEquals(expected, actual);
        }
    }

    @Test
    void testCubeUVFront() {
        var face = CoordinateMapper.Cube.Face.Front;
        var pointA = makePoint(-0.5, 0.5, 1);
        assertEquals(0.25, face.u(pointA));
        assertEquals(0.75, face.v(pointA));
        var pointB = makePoint(0.5, -0.5, 1);
        assertEquals(0.75, face.u(pointB));
        assertEquals(0.25, face.v(pointB));
    }

    @Test
    void testCubeUVBack() {
        var face = CoordinateMapper.Cube.Face.Back;
        var pointA = makePoint(0.5, 0.5, -1);
        assertEquals(0.25, face.u(pointA));
        assertEquals(0.75, face.v(pointA));
        var pointB = makePoint(-0.5, -0.5, -1);
        assertEquals(0.75, face.u(pointB));
        assertEquals(0.25, face.v(pointB));
    }

    @Test
    void testCubeUVLeft() {
        var face = CoordinateMapper.Cube.Face.Left;
        var pointA = makePoint(-1, 0.5, -0.5);
        assertEquals(0.25, face.u(pointA));
        assertEquals(0.75, face.v(pointA));
        var pointB = makePoint(-1, -0.5, 0.5);
        assertEquals(0.75, face.u(pointB));
        assertEquals(0.25, face.v(pointB));
    }

    @Test
    void testCubeUVRight() {
        var face = CoordinateMapper.Cube.Face.Right;
        var pointA = makePoint(1, 0.5, 0.5);
        assertEquals(0.25, face.u(pointA));
        assertEquals(0.75, face.v(pointA));
        var pointB = makePoint(1, -0.5, -0.5);
        assertEquals(0.75, face.u(pointB));
        assertEquals(0.25, face.v(pointB));
    }

    @Test
    void testCubeUVUp() {
        var face = CoordinateMapper.Cube.Face.Up;
        var pointA = makePoint(-0.5, 1, -0.5);
        assertEquals(0.25, face.u(pointA));
        assertEquals(0.75, face.v(pointA));
        var pointB = makePoint(0.5, 1, 0.5);
        assertEquals(0.75, face.u(pointB));
        assertEquals(0.25, face.v(pointB));
    }

    @Test
    void testCubeUVDown() {
        var face = CoordinateMapper.Cube.Face.Down;
        var pointA = makePoint(-0.5, -1, 0.5);
        assertEquals(0.25, face.u(pointA));
        assertEquals(0.75, face.v(pointA));
        var pointB = makePoint(0.5, -1, -0.5);
        assertEquals(0.75, face.u(pointB));
        assertEquals(0.25, face.v(pointB));
    }
}
