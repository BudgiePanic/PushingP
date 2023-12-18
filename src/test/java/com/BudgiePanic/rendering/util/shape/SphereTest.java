package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;

public class SphereTest {
    
    @Test
    void testSimpleSphereIntersect() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertTrue(intersects.isPresent());
        var list = intersects.get();
        assertEquals(2, list.size());
        assertTrue(compareFloat(4f, list.get(0).a()) == 0, "the distance to the first intersection point was not 4");
        assertTrue(compareFloat(6f, list.get(1).a()) == 0, "the distance to the second intersection point was not 6");
        assertTrue(list.get(0).sphere() == sphere, "the sphere reference in intersect 0 did not match the test sphere");
        assertTrue(list.get(1).sphere() == sphere, "the sphere reference in intersect 1 did not match the test sphere");
    }

    @Test
    void testSimpleSphereIntersectA() {
        var ray = new Ray(Tuple.makePoint(0, 1, -5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertTrue(intersects.isPresent());
        var list = intersects.get();
        assertEquals(2, list.size());
        assertTrue(compareFloat(5f, list.get(0).a()) == 0, "1. the distance to the intersection point was not 5");
        assertTrue(compareFloat(5f, list.get(1).a()) == 0, "2. the distance to the intersection point was not 5");
    }

    @Test
    void testSimpleSphereIntersectB() {
        var ray = new Ray(Tuple.makePoint(0, 2, -5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertTrue(intersects.isEmpty()); 
    }

    @Test
    void testSimpleSphereIntersectC() {
        var ray = new Ray(Tuple.makePoint(), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertTrue(intersects.isPresent());
        var list = intersects.get();
        assertEquals(2, list.size());
        assertTrue(compareFloat(-1f, list.get(0).a()) == 0, "the distance to the first intersection point was not -1");
        assertTrue(compareFloat(1f, list.get(1).a()) == 0, "the distance to the second intersection point was not 1");
    }

    @Test
    void testSimpleSphereIntersectD() {
        var ray = new Ray(Tuple.makePoint(0,0,5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Tuple.makePoint(), 1f);

        var intersects = sphere.intersect(ray);

        assertTrue(intersects.isPresent());
        var list = intersects.get();
        assertEquals(2, list.size());
        assertTrue(compareFloat(-6f, list.get(0).a()) == 0, "the distance to the first intersection point was not -6");
        assertTrue(compareFloat(-4f, list.get(1).a()) == 0, "the distance to the second intersection point was not -4");
    }

}
