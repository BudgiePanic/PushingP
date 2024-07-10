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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.shape.Triangle;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Test whether intersection distance from ray to shape is calculated correctly. 
 * Tests for correctness of intersection UV calculation, under and over point calculation, and reflection, refraction calculations.
 */
public class IntersectTest {
    
    @Test
    void testIntersect() {
        var sphere = Sphere.defaultSphere();
        var intersection = new Intersection(3.5, sphere);
        assertEquals(3.5f, intersection.a());
        assertTrue(sphere == intersection.shape());
    }

    // NOTE: the book wanted to add a test here for checking if an intersection test returned 2 intersections
    //       this test is unnecesary due to how we have coded it, currently because our function returns a list
    //       manually constructing a list, just to check if it has 2 elements in it is pointless.

    @Test
    void testIntersectHitA() {
        var sphere = Sphere.defaultSphere();
        var intersectA = new Intersection(1.0, sphere);
        var intersectB = new Intersection(2.0, sphere);
        var result = Intersection.Hit(List.of(intersectA, intersectB));
        assertTrue(result.isPresent(), "no hit intersection was returned");
        assertEquals(intersectA, result.get(), "first intersect did not equal the returned hit intersect");
        assertEquals(1f, result.get().a());
    }

    @Test
    void testIntersectHitB() {
        var sphere = Sphere.defaultSphere();
        var intersectA = new Intersection(-1.0, sphere);
        var intersectB = new Intersection(1.0, sphere);
        var result = Intersection.Hit(List.of(intersectA, intersectB));
        assertTrue(result.isPresent(), "no hit intersection was returned");
        assertEquals(intersectB, result.get());
    }

    @Test
    void testIntesectHitC() {
        var sphere = Sphere.defaultSphere();
        var intersectA = new Intersection(-2.0, sphere);
        var intersectB = new Intersection(-1.0, sphere);
        var result = Intersection.Hit(List.of(intersectA, intersectB));
        assertTrue(result.isEmpty(), "A hit intersection was returned when all distances were negative");
    }

    @Test
    void testIntersectHitD() {
        var sphere = Sphere.defaultSphere();
        var intersectA = new Intersection(5.0, sphere);
        var intersectB = new Intersection(7.0, sphere);
        var intersectC = new Intersection(-3.0, sphere);
        var intersectD = new Intersection(2.0, sphere);
        var result = Intersection.Hit(List.of(intersectA, intersectB, intersectC, intersectD));
        assertTrue(result.isPresent(), "no hit intersection was returned");
        assertEquals(intersectD, result.get());
    }

    @Test
    void testAcneAvoidance() {
        // test that intersection points are moved slightly above the surface along the surface normal for shadow testing
        // to prevent the ray intersection from being beneath the surface, falsely causing the system to think the 
        // ray hit a point in shadow, caused by floating point imprecision
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var shape = new Sphere(Transforms.identity().translate(0, 0, 1).assemble());
        var intersect = new Intersection(5.0, shape);
        var info = intersect.computeShadingInfo(ray);
        Tuple result = info.overPoint();
        assertTrue(result.z < -(FloatHelp.epsilon / 2.0)); 
        assertTrue(result.z < info.point().z); // the point moved along the normal should be closer to 0 (smaller) because the ray came from -ve z direction
        assertEquals(-1, FloatHelp.compareFloat(result.z, info.point().z));
    }

    @Test
    void testIntersectionPrecomputeReflectVector() {
        double sqrt2 = (Math.sqrt(2));
        var shape = new Plane(Transforms.identity().assemble());
        var ray = new Ray(Tuple.makePoint(0, 1, -1), Tuple.makeVector(0, -sqrt2/2f, sqrt2/2f));
        var intersection = new Intersection(sqrt2, shape);
        var info = intersection.computeShadingInfo(ray);
        var expected = Tuple.makeVector(0, sqrt2/2f, sqrt2/2f);
        assertEquals(expected, info.reflectVector());
    }

    @Test
    void testIntersectionRefraction() {
        // check that n1 and n2 values are correctly calculated in a test environment
        var material = Sphere.defaultGlassSphere().material();
        var a = new Sphere(Transforms.identity().scale(2, 2, 2).assemble(), material.setRefractiveIndex(1.5f));
        var b = new Sphere(Transforms.identity().translate(0, 0, -0.25f).assemble(), material.setRefractiveIndex(2f));
        var c = new Sphere(Transforms.identity().translate(0, 0, 0.25f).assemble(), material.setRefractiveIndex(2.5f));
        var ray = new Ray(makePoint(0, 0, -4), makeVector(0, 0, 1));
        var intersections = List.of(
            new Intersection(2.0, a),
            new Intersection(2.75, b),
            new Intersection(3.25, c),
            new Intersection(4.75, b),
            new Intersection(5.25, c),
            new Intersection(6.0, a)
        );
        var expectedN1 = List.of(
            1.0,
            1.5,
            2.0,
            2.5,
            2.5,
            1.5
        );
        var expectedN2 = List.of(
            1.5,
            2.0,
            2.5,
            2.5,
            1.5,
            1.0
        );
        for (int i = 0; i < intersections.size(); i++) {
            var expectedn1 = expectedN1.get(i);
            var expectedn2 = expectedN2.get(i);
            var intersection = intersections.get(i);
            var info = intersection.computeShadingInfo(ray, Optional.of(intersections));
            var n1 = info.n1();
            var n2 = info.n2();
            assertEquals(expectedn1, n1, intersection.toString() + i);
            assertEquals(expectedn2, n2, intersection.toString() + i);
        }
    }

    @Test
    void testUnderPointGeneration() {
        // under point lies slightly beneath the ray-object point of intersection, used to avoid acne caused by floating point errors
        var ray = new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1));
        var shape = new Sphere(Transforms.identity().translate(0, 0, 1).assemble(), Sphere.defaultGlassSphere().material());
        var intersection = new Intersection(5.0, shape);
        var info = intersection.computeShadingInfo(ray);
        var result = info.underPoint();
        assertTrue(result.z > (FloatHelp.epsilon * 0.5));
        assertTrue(info.point().z < result.z);
    }

    @Test
    void testSchlickUnderTotalInternalReflection() {
        var sqrt2Over2 = (Math.sqrt(2.0)/2.0);
        var shape = Sphere.defaultGlassSphere();
        var ray = new Ray(makePoint(0, 0, sqrt2Over2), makeVector(0, 1, 0));
        var intersections = Optional.of(List.of(
            new Intersection(-sqrt2Over2, shape),
            new Intersection(sqrt2Over2, shape)
        ));
        var info = intersections.get().getLast().computeShadingInfo(ray, intersections);
        var result = info.schlick();
        var expectedReflectance = 1.0;
        assertEquals(expectedReflectance, result);
    }

    @Test
    void testPerpendicularReflectance() {
        var shape = Sphere.defaultGlassSphere();
        var ray = new Ray(makePoint(), makeVector(0, 1, 0));
        var intersections = Optional.of(List.of(
            new Intersection(-1.0, shape),
            new Intersection(1.0, shape)
        ));
        var info = intersections.get().getLast().computeShadingInfo(ray, intersections);
        var result = info.schlick();
        var expectedReflectance = 0.04;
        assertEquals(0, FloatHelp.compareFloat(expectedReflectance, result));
    }

    @Test
    void testSmallAngleReflectance() {
        var shape = Sphere.defaultGlassSphere();
        var ray = new Ray(makePoint(0, 0.99f, -2f), makeVector(0, 0, 1));
        var intersections = Optional.of(List.of(
            new Intersection(1.8589, shape)
        ));
        var info = intersections.get().getFirst().computeShadingInfo(ray, intersections);
        var result = info.schlick();
        var expectedReflectance = 0.48873f;
        assertEquals(0, FloatHelp.compareFloat(expectedReflectance, result));
    }

    @Test
    void testIntersectionUVs() {
        // intersections with triangles should produce uv coordinates
        var triangle = new Triangle(makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0));
        var result = triangle.intersect(new Ray(makePoint(-0.2f, 0.3f, -2), makeVector(0, 0, 1)));
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(1, intersections.size());
        var intersecion = intersections.getFirst();
        assertTrue(intersecion.uv().isPresent());
        var uv = intersecion.uv().get();
        assertEquals(0, FloatHelp.compareFloat(uv.a(), 0.45f));
        assertEquals(0, FloatHelp.compareFloat(uv.b(), 0.25f));
    }
}
