package com.BudgiePanic.rendering.scene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

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
        assertEquals(sphereB.material().color(), result);
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
}
