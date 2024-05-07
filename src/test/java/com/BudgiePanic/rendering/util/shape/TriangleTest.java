package com.BudgiePanic.rendering.util.shape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;

public class TriangleTest {

    @Test
    void testCreateTriangle() {
        Tuple p1 = makePoint(0, 1, 0), p2 = makePoint(-1, 0, 0), p3 = makePoint(1, 0, 0);
        var triangle = new Triangle(p1, p2, p3);
        assertEquals(p1, triangle.p1);
        assertEquals(p2, triangle.p2);
        assertEquals(p3, triangle.p3);
        Tuple edge1 = makeVector(-1, -1, 0), edge2 = makeVector(1, -1, 0);
        var normal = makeVector(0, 0, -1);
        assertEquals(edge1, triangle.edge1);
        assertEquals(edge2, triangle.edge2); // TODO do these fields need getters?
        assertEquals(normal, triangle.normal);
    }

    @Test
    void testTriangleNormal() {
        // triangle's precompute their normal
        var triangle = new Triangle(makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0));
        assertEquals(triangle.normal, triangle.localNormal(makePoint(0, 0.5f, 0)));
        assertEquals(triangle.normal, triangle.localNormal(makePoint(-0.5f, 0.75f, 0)));
        assertEquals(triangle.normal, triangle.localNormal(makePoint(0.5f, 0.25f, 0)));
    }

    @Test
    void testTriangleIntersection() { // parrallel miss
        var triangle = new Triangle(makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0));
        var ray = new Ray(makePoint(0, -1, -2), makeVector(0, 1, 0));
        var result = triangle.localIntersect(ray);
        assertTrue(result.isEmpty());
    }

    @Test
    void testTriangleIntersectionA () { // p1-p3 edge
        var triangle = new Triangle(makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0));
        var ray = new Ray(makePoint(1, 1, -2), makeVector(0, 0, 1));
        var result = triangle.localIntersect(ray);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testTriangleIntersectionB () { // p1-p2 edge
        var triangle = new Triangle(makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0));
        var ray = new Ray(makePoint(-1, 1, -2), makeVector(0, 0, 1));
        var result = triangle.localIntersect(ray);
        assertTrue(result.isEmpty());
    }

    @Test
    void testTriangleIntersectionC () { // p2-p3 edge
        var triangle = new Triangle(makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0));
        var ray = new Ray(makePoint(0, -1, -2), makeVector(0, 0, 1));
        var result = triangle.localIntersect(ray);
        assertTrue(result.isEmpty());
    }

    @Test
    void testTriangleIntersectionD () { // hit
        var triangle = new Triangle(makePoint(0, 1, 0), makePoint(-1, 0, 0), makePoint(1, 0, 0));
        var ray = new Ray(makePoint(0, 0.5f, -2), makeVector(0, 0, 1));
        var result = triangle.localIntersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(1, intersections.size());
        assertTrue(FloatHelp.compareFloat(2f, intersections.get(0).a()) == 0);
    }

    @Test
    void testTriangleSolid() {
        var triangle = new Triangle(makePoint(1, 0, 0), makePoint(0, 1, 0), makePoint(0, 0, 1));
        assertFalse(triangle.isSolid());
    }

    @Test
    void testTriangleDivide() {
        var shape = new Triangle(makePoint(1, 0, 0), makePoint(0, 1, 0), makePoint(0, 0, 1));
        var result = shape.divide(0);
        assertEquals(shape, result);
    }

}
