package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Intersection;

public class SmoothTriangleTest {

    static final SmoothTriangle testTriangle = new SmoothTriangle(
        makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0),
        makeVector(0, 1, 0), makeVector(-1, 0, 0), makeVector(1, 0, 0));

    @Test
    void testSmoothTriangleProperties() {
        var p1 = makePoint(0, 1, 0);
        var p2 = makePoint(-1, 0, 0);
        var p3 = makePoint(1, 0, 0);
        var n1 = makeVector(0, 1, 0);
        var n2 = makeVector(-1, 0, 0);
        var n3 = makeVector(1, 0, 0);
        var triangle = new SmoothTriangle(p1, p2, p3, n1, n2, n3);
        assertEquals(p1, triangle.p1());
        assertEquals(p2, triangle.p2());
        assertEquals(p3, triangle.p3());
        assertEquals(n1, triangle.normal1());
        assertEquals(n2, triangle.normal2());
        assertEquals(n3, triangle.normal3());
    }

    @Test
    void testLocalNormal() {
        // use intersection UVs to interpolate the normal
        var intersection = new Intersection(1.0, testTriangle, new Pair<>(0.45, 0.25));
        var normal = testTriangle.normal(makePoint(), intersection);
        var expected = makeVector(-0.5547f, 0.83205f, 0);
        assertEquals(expected, normal);
    }
    
}
