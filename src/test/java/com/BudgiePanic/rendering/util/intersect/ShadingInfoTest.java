package com.BudgiePanic.rendering.util.intersect;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
