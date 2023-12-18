package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.Translation;

public class SphereTest {
    
    @Test
    void testSimpleSphereIntersect() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var sphere = Sphere.defaultSphere();

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
        var sphere = Sphere.defaultSphere();

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
        var sphere = Sphere.defaultSphere();

        var intersects = sphere.intersect(ray);

        assertTrue(intersects.isEmpty()); 
    }

    @Test
    void testSimpleSphereIntersectC() {
        var ray = new Ray(Tuple.makePoint(), Tuple.makeVector(0, 0, 1));
        var sphere = Sphere.defaultSphere();

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
        var sphere = Sphere.defaultSphere();

        var intersects = sphere.intersect(ray);

        assertTrue(intersects.isPresent());
        var list = intersects.get();
        assertEquals(2, list.size());
        assertTrue(compareFloat(-6f, list.get(0).a()) == 0, "the distance to the first intersection point was not -6");
        assertTrue(compareFloat(-4f, list.get(1).a()) == 0, "the distance to the second intersection point was not -4");
    }

    @Test
    void testSphereDefaultConstructor() {
        var sphere = Sphere.defaultSphere();
        assertEquals(Matrix4.identity(), sphere.transform());
    }

    @Test
    void testSphereTransformUpdate() {
        // The book wants a test here to check if the transform of a sphere can be mutated
        // but our spheres are immutable so such a test doesn't make much sense.
        // In our case, the solution is to just make a new sphere.
        var sphere = new Sphere(Transforms.identity().translate(2, 3, 4).assemble());
        var result = sphere.transform();
        var expected = Translation.makeTranslationMatrix(2, 3, 4);
        assertEquals(expected, result);
    }

    @Test
    void testSphereRayTransform() {
        var ray = new Ray(Tuple.makePoint(0,0,-5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Transforms.identity().scale(2, 2, 2).assemble());
        var intersections = sphere.intersect(ray);
        assertTrue(intersections.isPresent());
        var list = intersections.get();
        assertEquals(2, list.size());
        assertTrue(compareFloat(3, list.get(0).a())== 0, "first ray intersection distance was not 3");
        assertTrue(compareFloat(7, list.get(1).a())== 0, "second ray intersection distance was not 7");
    }

    @Test
    void testSphereRayTransformA() {
        var ray = new Ray(Tuple.makePoint(0,0,5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Transforms.identity().translate(5, 0, 0).assemble());
        var intersections = sphere.intersect(ray);
        assertTrue(intersections.isEmpty());
    }
}
