package com.BudgiePanic.rendering.scene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
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
}
