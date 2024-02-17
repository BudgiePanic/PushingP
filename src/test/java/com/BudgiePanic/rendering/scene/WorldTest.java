package com.BudgiePanic.rendering.scene;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.PatternTest;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class WorldTest {
    
    /**
     * The default test world.
     */
    static World defaultTestWorld;

    @BeforeAll
    static void initializeTestWorld() {
        var light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        var sphereA = new Sphere(
            Transforms.identity().assemble(),
            Material.color(
                new Color(0.8f, 1.0f, 0.6f)).
                  setDiffuse(0.7f).
                  setSpecular(0.2f)
            );
        var sphereB = new Sphere(Transforms.identity().scale(0.5f, 0.5f, 0.5f).assemble());
        defaultTestWorld = new World();
        defaultTestWorld.addLight(light);
        defaultTestWorld.addShape(sphereA);
        defaultTestWorld.addShape(sphereB);
    }

    @Test
    void testWorldInitialization() {
        // a new world should have no light sources and no objects
        var world = new World();
        assertTrue(world.getLights().isEmpty());
        assertTrue(world.getShapes().isEmpty());
    }

    @Test
    void testWorldRayIntersection() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var result = defaultTestWorld.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(4, intersections.size());
        assertEquals(4, intersections.get(0).a());
        assertEquals(4.5f, intersections.get(1).a());
        assertEquals(5.5f, intersections.get(2).a());
        assertEquals(6, intersections.get(3).a());
    }

    @Test
    void testWorldShading() {
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0,0,1));
        final var first = 0;
        var shape = defaultTestWorld.getShapes().get(first);
        var intersection = new Intersection(4f, shape);
        var info = intersection.computeShadingInfo(ray);
        var result = defaultTestWorld.shadeHit(info);
        assertEquals(new Color(0.38066f, 0.47583f, 0.2855f), result);
    }

    @Test
    void testWorldShadingInsideShape() {
        var world = new World();
        var light = new PointLight(Tuple.makePoint(0,0.25f,0), Colors.white);
        var sphereA = new Sphere(
            Transforms.identity().assemble(),
            Material.color(
                new Color(0.8f, 1.0f, 0.6f)).
                  setDiffuse(0.7f).
                  setSpecular(0.2f)
            );
        var sphereB = new Sphere(Transforms.identity().scale(0.5f, 0.5f, 0.5f).assemble());
        world.addLight(light);
        world.addShape(sphereA);
        world.addShape(sphereB);

        var ray = new Ray(Tuple.makePoint(), Tuple.makeVector(0, 0, 1));
        var shape = world.shapes.get(1);
        var intersection = new Intersection(0.5f, shape);
        var info = intersection.computeShadingInfo(ray);
        var result = world.shadeHit(info);
        assertEquals(new Color(0.90498f, 0.90498f, 0.90498f), result);
    }

    // ====== Test that world can automatically take a ray and determine the color produced by that ray ======
    @Test
    void testWorldColorRay() {
        // ray miss
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 1, 0));
        var result = defaultTestWorld.computeColor(ray);
        assertEquals(Colors.black, result);
    }

    @Test
    void testWorldColorRayA() {
        // ray hit
        var ray = new Ray(Tuple.makePoint(0, 0, -5), Tuple.makeVector(0, 0, 1));
        var result = defaultTestWorld.computeColor(ray);
        assertEquals(new Color(0.38066f, 0.47583f, 0.2855f), result);
    }

    @Test
    void testWorldColorRayB() {
        // ray hit from behind
        var world = new World();
        var light = new PointLight(Tuple.makePoint(0,0.25f,0), Colors.white);
        var sphereA = new Sphere(
            Transforms.identity().assemble(),
            Material.color(
                new Color(0.8f, 1.0f, 0.6f)).
                  setDiffuse(0.7f).
                  setSpecular(0.2f).
                  setAmbient(1f)
            );
        var sphereB = new Sphere(
            Transforms.identity().scale(0.5f, 0.5f, 0.5f).assemble(),
            Material.defaultMaterial().setAmbient(1f)
        );
        world.addLight(light);
        world.addShape(sphereA);
        world.addShape(sphereB);

        var ray = new Ray(Tuple.makePoint(0, 0, 0.75f), Tuple.makeVector(0, 0, -1));
        var result = world.computeColor(ray);
        assertEquals(sphereB.material().pattern().colorAt(Tuple.makePoint()), result);
    }

    @Test
    void testIlluminatedPoint() {
        var result = defaultTestWorld.inShadow(Tuple.makePoint(0, 10, 0));
        assertFalse(result);
    }

    @Test
    void testShadowedPoint() {
        var result = defaultTestWorld.inShadow(Tuple.makePoint(10, -10, 10));
        assertTrue(result);
    }

    @Test
    void testIlluminatedPointA() {
        var result = defaultTestWorld.inShadow(Tuple.makePoint(-20, 20, 20));
        assertFalse(result);
    }

    @Test
    void testIlluminatedPointB () {
        var result = defaultTestWorld.inShadow(Tuple.makePoint(-2, 2, 2));
        assertFalse(result);
    }

    @Test
    void testShadeHitInShadow() {
        var world = new World();
        world.addLight(new PointLight(Tuple.makePoint(0, 0, -10), Colors.white));
        world.addShape(Sphere.defaultSphere());
        world.addShape(new Sphere(Transforms.identity().translate(0, 0, 10).assemble()));
        var ray = new Ray(Tuple.makePoint(0, 0, 5), Tuple.makeVector(0, 0, 1));
        var intersection = new Intersection(4f, world.getShapes().get(1));
        var info = intersection.computeShadingInfo(ray);
        var result = world.shadeHit(info);
        var expected = new Color(0.1f, 0.1f, 0.1f);
        assertEquals(expected, result);
    }

    @Test
    void testShadingThinObject() {
        // there should be no acne effect, all intersection points should NOT be in shadow
        var floor = new Sphere(Transforms.identity().scale(10, 0.01f, 10).assemble(),
            Material.color(new Color(1, 0.9f, 0.9f)).setSpecular(0));
        Sphere leftWall = new Sphere(
            Transforms.identity().
                scale(10, 0.1f, 10).
                rotateX(AngleHelp.toRadians(90)).
                rotateY(AngleHelp.toRadians(-45)).
                translate(0, 0, 5).assemble(),
            floor.material()
        );
        Sphere rightWall = new Sphere(
            Transforms.identity().
                scale(10, 0.1f, 10).
                rotateX(AngleHelp.toRadians(90)).
                rotateY(AngleHelp.toRadians(45)).
                translate(0, 0, 5).assemble(),
            floor.material()
        );
        PointLight light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        World world = new World();
        world.addLight(light);
        world.addShape(floor);
        world.addShape(leftWall);
        world.addShape(rightWall);
        Camera camera = new Camera(50, 50, 
            AngleHelp.toRadians(90), 
            View.makeViewMatrix(
                Tuple.makePoint(0, 1.5f, -5f),
                Tuple.makePoint(0, 1, 0),
                Tuple.makeVector(0, 1, 0)));
        for (int row = 0; row < camera.height; row++) {
            for (int col = 0; col < camera.width; col++) {
                var ray = camera.createRay(col, row);
                // get shading info from every ray
                var intersections = world.intersect(ray);
                assertTrue(intersections.isPresent(), String.format("ray for pixel r:%d c:%d did not intersect with any objects in the scene", row, col));
                var hit = Intersection.Hit(intersections.get());
                assertTrue(hit.isPresent(), String.format("ray for pixel r:%d c:%d intersected, but had no hit", row, col));
                // ask if the ray is in shadow
                // in this test set up, no intersection points should be in shadow
                var hitInfo = hit.get().computeShadingInfo(ray);
                var inShadow = world.inShadow(hitInfo.overPoint());
                assertFalse(inShadow, String.format("ray at r:%d c:%d was in shadow", row, col));
            }
        }
    }

    @Test
    void testNonReflectiveSurface() {
        var ray = new Ray(Tuple.makePoint(), Tuple.makeVector(0, 0, 1));
        var material = new Material(Colors.white, 1,0,0,0,0,0,0);
        
        var shape = new Sphere(defaultTestWorld.getShapes().get(1).transform(), material); 
        defaultTestWorld.getShapes().remove(1);
        defaultTestWorld.getShapes().add(shape);

        var intersection = new Intersection(1f, shape);

        var info = intersection.computeShadingInfo(ray);
        var output = defaultTestWorld.shadeReflection(info);
        var expected = Colors.black;
        assertEquals(expected, output);
    }

    @Test
    void testReflectiveSurface() {
        var plane = new Plane( Transforms.identity().translate(0, -1, 0).assemble(), Material.defaultMaterial().setReflectivity(0.5f));
        defaultTestWorld.addShape(plane);
        float sqrt2 = (float)(Math.sqrt(2));
        float sqrt2over2 = (float)(Math.sqrt(2)/2.0);
        var ray = new Ray(makePoint(0, 0, -3), makeVector(0, -sqrt2over2, sqrt2over2));
        var intersection = new Intersection(sqrt2, plane);
        var info = intersection.computeShadingInfo(ray);
        var output = defaultTestWorld.shadeReflection(info);
        // we get slightly different color than the author due to floating point rounding errors, so we'll use big epslion for this test
        var expected = new Color(0.19032f, 0.2379f, 0.14274f);
        // tidy up mutations to default test world
        defaultTestWorld.shapes.removeLast();
        assertEquals(expected, output);
    }

    @Test
    void testReflectPlusShade() {
        var plane = new Plane( Transforms.identity().translate(0, -1, 0).assemble(), Material.defaultMaterial().setReflectivity(0.5f));
        defaultTestWorld.addShape(plane);
        float sqrt2 = (float)(Math.sqrt(2));
        float sqrt2over2 = (float)(Math.sqrt(2)/2.0);
        var ray = new Ray(makePoint(0, 0, -3), makeVector(0, -sqrt2over2, sqrt2over2));
        var intersection = new Intersection(sqrt2, plane);
        var info = intersection.computeShadingInfo(ray);
        var output = defaultTestWorld.shadeHit(info);
        // we get slightly different color than the author due to floating point rounding errors, so we'll use big epslion for this test
        var expected = new Color(0.87677f, 0.92436f, 0.82918f);
        // tidy up mutations to default test world
        defaultTestWorld.shapes.removeLast();
        assertEquals(expected, output);
    }

    @Test
    void testReflectRecursion() {
        assertDoesNotThrow(() -> {
            var world = new World();
            var light = new PointLight(makePoint(), Colors.white);
            var material = new Material(Colors.white, 1, 0, 0, 0, 1, 0, 0);
            var floor = new Plane(Transforms.identity().translate(0, -1, 0).assemble(), material);
            var ceiling = new Plane(Transforms.identity().translate(0, 1, 0).assemble(), material);
            world.addLight(light);
            world.addShape(floor);
            world.addShape(ceiling);
            var ray = new Ray(makePoint(), makeVector(0, 1, 0));
            world.computeColor(ray);
        });
    }

    @Test
    void testReflectionRecursionLimit() {
        var plane = new Plane( Transforms.identity().translate(0, -1, 0).assemble(), Material.defaultMaterial().setReflectivity(0.5f));
        defaultTestWorld.addShape(plane);
        float sqrt2 = (float)(Math.sqrt(2));
        float sqrt2over2 = (float)(Math.sqrt(2)/2.0);
        var ray = new Ray(makePoint(0, 0, -3), makeVector(0, -sqrt2over2, sqrt2over2));
        var intersection = new Intersection(sqrt2, plane);
        var info = intersection.computeShadingInfo(ray);
        var output = defaultTestWorld.shadeReflection(info, 0);
        var expected = Colors.black;
        // tidy up mutations to default test world
        defaultTestWorld.shapes.removeLast();
        assertEquals(expected, output);
    }

    @Test
    void testRefractionOpaqueShape() {
        // an opaque shape does not refract, so black should be returned by the refraction function when refraction testing an opaque object
        var shape = defaultTestWorld.shapes.getFirst();
        var ray = new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1));
        var intersections = Optional.of(
            List.of(
                new Intersection(4f, shape),
                new Intersection(6f, shape)
            ) 
        );
        var info = intersections.get().getFirst().computeShadingInfo(ray, intersections);
        var result = defaultTestWorld.shadeRefraction(info);
        assertEquals(Colors.black, result);
    }

    @Test
    void testRefractionRecusionLimit() {
        var shape = defaultTestWorld.shapes.getFirst();
        var replacement = new Sphere(shape.transform(), shape.material().setTransparency(1).setRefractiveIndex(1.5f));
        defaultTestWorld.shapes.removeFirst();
        defaultTestWorld.shapes.add(0, replacement);

        var ray = new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1));
        var intersections = Optional.of(
            List.of(
                new Intersection(4f, shape),
                new Intersection(6f, shape)
            ) 
        );
        var info = intersections.get().getFirst().computeShadingInfo(ray, intersections);
        var result = defaultTestWorld.shadeRefraction(info, 0);
        assertEquals(Colors.black, result);

        // tidy up
        defaultTestWorld.shapes.remove(replacement);
        defaultTestWorld.shapes.add(0, shape);
    }

    @Test
    void testRefractionTotalInternalReflection() {
        // when total internal reflection occurs, the ray tracer should not investigate further reflections
        var shape = defaultTestWorld.shapes.getFirst();
        var replacement = new Sphere(shape.transform(), shape.material().setTransparency(1).setRefractiveIndex(1.5f));
        defaultTestWorld.shapes.removeFirst();
        defaultTestWorld.shapes.add(0, replacement);

        float sqrt2Over2 = (float) (Math.sqrt(2.0) / 2.0);
        var ray = new Ray(makePoint(0, 0, sqrt2Over2), makeVector(0, 1, 0));
        var intersections = Optional.of(
            List.of(
                new Intersection(-sqrt2Over2, shape),
                new Intersection(sqrt2Over2, shape)
            ) 
        );
        var info = intersections.get().get(1).computeShadingInfo(ray, intersections);
        var result = defaultTestWorld.shadeRefraction(info);
        // tidy up
        defaultTestWorld.shapes.remove(replacement);
        defaultTestWorld.shapes.add(0, shape);

        assertEquals(Colors.black, result);
    }

    @Test
    void testRefractionTransparentShape() {
        var light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        var sphereA = new Sphere(
            Transforms.identity().assemble(),
            new Material(new PatternTest.TestPattern(), 1, 0, 0, 0, 0, 0, 1)
        );
        var sphereB = new Sphere(
            Transforms.identity().scale(0.5f, 0.5f, 0.5f).assemble(),
            Sphere.defaultGlassSphere().material()
        );
        var world = new World();
        world.addLight(light);
        world.addShape(sphereA);
        world.addShape(sphereB);

        var ray = new Ray(makePoint(0, 0, 0.1f), makeVector(0, 1, 0));
        var intersections = Optional.of(
            List.of(
                new Intersection(-0.9899f, sphereA),
                new Intersection(-0.4899f, sphereB),
                new Intersection(0.4899f, sphereB),
                new Intersection(0.9899f, sphereA)
            ) 
        );
        var info = intersections.get().get(2).computeShadingInfo(ray, intersections);
        var result = world.shadeRefraction(info);
        var expected = new Color(0, 0.99888f, 0.04725f);
        assertEquals(expected, result);
    } 

    @Test
    void testHitShadeTransparentShape() {
        var light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        var floor = new Plane(
            Transforms.identity().translate(0, -1, 0).assemble(),
            Material.defaultMaterial().setTransparency(0.5f).setRefractiveIndex(1.5f)
        );
        var orb = new Sphere(
            Transforms.identity().translate(0, -3.5f, -0.5f).assemble(),
            Material.color(Colors.red).setAmbient(0.5f)
        );
        var world = new World();
        world.addLight(light);
        world.addShape(floor);
        world.addShape(orb);
        float sqrt2 = (float) Math.sqrt(2.0);
        float sqrt2Over2 = (float) (Math.sqrt(2.0)/2.0);
        var ray = new Ray(makePoint(0, 0, -3), makeVector(0, -sqrt2Over2, sqrt2Over2));
        var intersections = Optional.of(
            List.of(
                new Intersection(sqrt2, floor)
            ) 
        );
        var info = intersections.get().getFirst().computeShadingInfo(ray, intersections);
        var result = world.shadeHit(info);
        var expected = new Color(0.93642f, 0.68642f, 0.68642f);
        // extra info in case of test failure
        float x = result.x - expected.x, y = result.y - expected.y, z = result.z - expected.z;
        assertEquals(expected, result, x + " " + y + " " + z);
    }

    @Test
    void testSchlickTransparentReflectiveShape() {
        var light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        var floor = new Plane(
            Transforms.identity().translate(0, -1, 0).assemble(),
            Material.defaultMaterial().setTransparency(0.5f).setRefractiveIndex(1.5f).setReflectivity(0.5f)
        );
        var orb = new Sphere(
            Transforms.identity().translate(0, -3.5f, -0.5f).assemble(),
            Material.color(Colors.red).setAmbient(0.5f)
        );
        var sphereA = new Sphere(
            Transforms.identity().assemble(),
            Material.color(
                new Color(0.8f, 1.0f, 0.6f)).
                  setDiffuse(0.7f).
                  setSpecular(0.2f)
            );
        var world = new World();
        world.addLight(light);
        world.addShape(floor);
        world.addShape(orb);
        world.addShape(sphereA);
        float sqrt2 = (float) Math.sqrt(2.0);
        float sqrt2Over2 = (float) (Math.sqrt(2.0)/2.0);
        var ray = new Ray(makePoint(0, 0, -3), makeVector(0, -sqrt2Over2, sqrt2Over2));
        var intersections = Optional.of(
            List.of(
                new Intersection(sqrt2, floor)
            ) 
        );
        var info = intersections.get().getFirst().computeShadingInfo(ray, intersections);
        var result = world.shadeHit(info);
        var expected = new Color(0.93391f, 0.69643f, 0.69243f);
        assertEquals(expected, result);
    }
}
