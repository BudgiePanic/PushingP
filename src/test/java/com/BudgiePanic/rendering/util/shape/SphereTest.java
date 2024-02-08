package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Material;
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
        var ray = new Ray(Tuple.makePoint(0,0,-5), Tuple.makeVector(0, 0, 1));
        var sphere = new Sphere(Transforms.identity().translate(5, 0, 0).assemble());
        var intersections = sphere.intersect(ray);
        assertTrue(intersections.isEmpty());
    }

    @Test
    void testSphereNormalX() {
        var sphere = Sphere.defaultSphere();
        var normal = sphere.normal(Tuple.makePoint(1,0,0));
        var expected = Tuple.makeVector(1, 0, 0);
        assertEquals(expected, normal);
    }

    @Test
    void testSphereNormalZ() {
        var sphere = Sphere.defaultSphere();
        var normal = sphere.normal(Tuple.makePoint(0,0,1));
        var expected = Tuple.makeVector(0, 0, 1);
        assertEquals(expected, normal);
    }

    @Test
    void testSphereNormalY() {
        var sphere = Sphere.defaultSphere();
        var normal = sphere.normal(Tuple.makePoint(0, 1,0));
        var expected = Tuple.makeVector(0, 1, 0);
        assertEquals(expected, normal);
    }

    @Test
    void testSphereNormal() {
        var sqrtThreeOverThree = (float) (Math.sqrt(3.0) / 3.0);
        var sphere = Sphere.defaultSphere();
        var normal = sphere.normal(Tuple.makePoint(sqrtThreeOverThree,sqrtThreeOverThree,sqrtThreeOverThree));
        var expected = Tuple.makeVector(sqrtThreeOverThree,sqrtThreeOverThree,sqrtThreeOverThree);
        assertEquals(expected, normal);
    }

    @Test
    void testNormalMagnitude() {
        var sqrtThreeOverThree = (float) (Math.sqrt(3.0) / 3.0);
        var sphere = Sphere.defaultSphere();
        var normal = sphere.normal(Tuple.makePoint(sqrtThreeOverThree,sqrtThreeOverThree,sqrtThreeOverThree));
        var expected = normal.normalize();
        assertEquals(expected, normal);
    }

    @Test
    void testNormalNonOrigin() {
        var sphere = new Sphere(Translation.makeTranslationMatrix(0, 1, 0));
        var normal = sphere.normal(Tuple.makePoint(0f, 1.70711f, -0.70711f));
        var expected = Tuple.makeVector(0f, 0.70711f, -0.70711f);
        assertEquals(expected, normal);
    }

    @Test
    void testNormalTransformedOrigin() {
        var piOverFive = (float)(Math.PI / 5.0);
        var sqrtTwoOverTwo = (float)(Math.sqrt(2.0) / 2.0);
        var sphere = new Sphere(Transforms.identity().rotateZ(piOverFive).scale(1f, 0.5f, 1f).assemble());
        var normal = sphere.normal(Tuple.makePoint(0f, sqrtTwoOverTwo, -sqrtTwoOverTwo));
        var expected = Tuple.makeVector(0f, 0.97014f, -0.24254f);
        assertEquals(expected, normal); 
    }

    @Test
    void testSphereMaterialProperty() {
        assertDoesNotThrow(()->{
            var sphere = new Sphere(
                            Matrix4.identity(), 
                            new Material(new Color(), 1f, 0f,0f,0f,0f));
            assertEquals(1f, sphere.material().ambient());
        });
    }
}
