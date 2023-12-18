package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Ray;
import com.BudgiePanic.rendering.util.Tuple;

public class SphereTest {
    
    @Test
    void testSimpleSphereIntersect() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertEquals(2, intersects.size());
        assertTrue(compareFloat(4f, intersects.get(0)) == 0, "the distance to the first intersection point was not 4");
        assertTrue(compareFloat(6f, intersects.get(1)) == 0, "the distance to the second intersection point was not 6");
    }

    @Test
    void testSimpleSphereIntersectA() {
        var ray = new Ray(Tuple.makePoint(0, 1, -5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertEquals(2, intersects.size()); // This test may break later?
        assertTrue(compareFloat(5f, intersects.get(0)) == 0, "1. the distance to the intersection point was not 5");
        assertTrue(compareFloat(5f, intersects.get(1)) == 0, "2. the distance to the intersection point was not 5");
    }

    @Test
    void testSimpleSphereIntersectB() {
        var ray = new Ray(Tuple.makePoint(0, 2, -5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertEquals(0, intersects.size()); 
    }

    @Test
    void testSimpleSphereIntersectC() {
        var ray = new Ray(Tuple.makePoint(), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertEquals(2, intersects.size());
        assertTrue(compareFloat(-1f, intersects.get(0)) == 0, "the distance to the first intersection point was not -1");
        assertTrue(compareFloat(1f, intersects.get(1)) == 0, "the distance to the second intersection point was not 1");
    }

    @Test
    void testSimpleSphereIntersectD() {
        var ray = new Ray(Tuple.makePoint(0,0,5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertEquals(2, intersects.size());
        assertTrue(compareFloat(-6f, intersects.get(0)) == 0, "the distance to the first intersection point was not -6");
        assertTrue(compareFloat(-4f, intersects.get(1)) == 0, "the distance to the second intersection point was not -4");
    }

}
