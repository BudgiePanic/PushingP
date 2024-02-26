package com.BudgiePanic.rendering.util.intersect;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.SmoothTriangle;
import com.BudgiePanic.rendering.util.shape.Sphere;

public class ShadingInfoTest {
    
    @Test
    void testIntersectionPrecompute() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var shape = Sphere.defaultSphere();
        var intersection = new Intersection(4f, shape);
        var result = intersection.computeShadingInfo(ray);

        assertEquals(intersection.a(), result.a());
        assertEquals(intersection.shape(), result.shape());
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

    @Test
    void testIntersectionComputeSmoothTriangle() {
        var testTriangle = new SmoothTriangle(
        makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0),
        makeVector(0, 1, 0), makeVector(-1, 0, 0), makeVector(1, 0, 0));
        var intersection = new Intersection(1f, testTriangle, new Pair<Float, Float>(0.45f, 0.25f));
        var ray = new Ray(makePoint(-0.2f, 0.3f, -2), makeVector(0, 0, 1));
        var info = intersection.computeShadingInfo(ray);
        var expected = makeVector(-0.5547f, 0.83205f, 0);
        assertEquals(expected, info.normalVector());
    }
}
