package com.BudgiePanic.rendering.util.intersect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class IntersectTest {
    
    @Test
    void testIntersect() {
        var sphere = Sphere.defaultSphere();
        var intersection = new Intersection(3.5f, sphere);
        assertEquals(3.5f, intersection.a());
        assertTrue(sphere == intersection.shape());
    }

    // NOTE: the book wanted to add a test here for checking if an intersection test returned 2 intersections
    //       this test is unnecesary due to how we have coded it, currently because our function returns a list
    //       manually constructing a list, just to check if it has 2 elements in it is pointless.

    @Test
    void testIntersectHitA() {
        var sphere = Sphere.defaultSphere();
        var intersectA = new Intersection(1f, sphere);
        var intersectB = new Intersection(2f, sphere);
        var result = Intersection.Hit(List.of(intersectA, intersectB));
        assertTrue(result.isPresent(), "no hit intersection was returned");
        assertEquals(intersectA, result.get(), "first intersect did not equal the returned hit intersect");
        assertEquals(1f, result.get().a());
    }

    @Test
    void testIntersectHitB() {
        var sphere = Sphere.defaultSphere();
        var intersectA = new Intersection(-1f, sphere);
        var intersectB = new Intersection(1f, sphere);
        var result = Intersection.Hit(List.of(intersectA, intersectB));
        assertTrue(result.isPresent(), "no hit intersection was returned");
        assertEquals(intersectB, result.get());
    }

    @Test
    void testIntesectHitC() {
        var sphere = Sphere.defaultSphere();
        var intersectA = new Intersection(-2f, sphere);
        var intersectB = new Intersection(-1f, sphere);
        var result = Intersection.Hit(List.of(intersectA, intersectB));
        assertTrue(result.isEmpty(), "A hit intersection was returned when all distances were negative");
    }

    @Test
    void testIntersectHitD() {
        var sphere = Sphere.defaultSphere();
        var intersectA = new Intersection(5f, sphere);
        var intersectB = new Intersection(7f, sphere);
        var intersectC = new Intersection(-3f, sphere);
        var intersectD = new Intersection(2f, sphere);
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
        var intersect = new Intersection(5f, shape);
        var info = intersect.computeShadingInfo(ray);
        Tuple result = info.overPoint();
        assertTrue(result.z < -(FloatHelp.epsilon / 2f)); 
        assertTrue(result.z < info.point().z); // the point moved along the normal should be closer to 0 (smaller) because the ray came from -ve z direction
        assertEquals(-1, FloatHelp.compareFloat(result.z, info.point().z));
        assertEquals(-1, FloatHelp.compareFloat(result.z, -(FloatHelp.epsilon / 2f)));
    }

}
