package com.BudgiePanic.rendering.util.intersect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.Sphere;

public class ShadingInfoTest {
    
    @Test
    void testIntersectionPrecompute() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var shape = Sphere.defaultSphere();
        var intersection = new Intersection(4f, shape);
        var result = intersection.computeShadingInfo(ray);

        assertEquals(intersection.a(), result.a());
        assertEquals(intersection.sphere(), result.shape());
        assertEquals(Tuple.makePoint(0, 0, -1), result.point());
        assertEquals(Tuple.makeVector(0, 0, -1), result.eyeVector());
        assertEquals(Tuple.makeVector(0, 0, -1), result.normalVector());
    }

    @Test
    void testIntersectionOutsideShape() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var shape = Sphere.defaultSphere();
        var intersection = new Intersection(4f, shape);
        var result = intersection.computeShadingInfo(ray);
        assertFalse(result.intersectInside());
    }

    @Test
    void testIntersectionInsideShape() {
        var ray = new Ray(Tuple.makePoint(), Tuple.makeVector(0, 0, 1));
        var shape = Sphere.defaultSphere();
        var intersection = new Intersection(1f, shape);
        var result = intersection.computeShadingInfo(ray);
        assertTrue(result.intersectInside());
        assertEquals(Tuple.makePoint(0, 0, 1), result.point());
        assertEquals(Tuple.makeVector(0, 0, -1), result.eyeVector());
        assertEquals(Tuple.makeVector(0, 0, -1), result.normalVector());
    }
}
