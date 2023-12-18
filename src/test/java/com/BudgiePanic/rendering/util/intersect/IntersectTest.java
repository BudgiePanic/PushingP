package com.BudgiePanic.rendering.util.intersect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.Sphere;

public class IntersectTest {
    
    @Test
    void testIntersect() {
        var sphere = new Sphere(Tuple.makePoint(), 1f);
        var intersection = new Intersection(3.5f, sphere);
        assertEquals(3.5f, intersection.a());
        assertTrue(sphere == intersection.sphere());
    }

    // NOTE: the book wanted to add a test here for checking if an intersection test returned 2 intersections
    //       this test is unnecesary due to how we have coded it, currently
    //       In the future, if we wanted our intersection test to return an arbitrary number of intersection points, then this test would be needed.

    @Test
    void testIntersectHitA() {
        var sphere = new Sphere(Tuple.makePoint(), 1f);
        var intersectA = new Intersection(1f, sphere);
        var intersectB = new Intersection(2f, sphere);
        var result = Intersection.Hit(List.of(intersectA, intersectB));
        assertTrue(result.isPresent(), "no hit intersection was returned");
        assertEquals(intersectA, result.get(), "first intersect did not equal the returned hit intersect");
        assertEquals(1f, result.get().a());
    }
}
