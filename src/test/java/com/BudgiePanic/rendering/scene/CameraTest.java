package com.BudgiePanic.rendering.scene;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class CameraTest {
    
    @Test
    void testCameraConstructor() {
        var width = 160;
        var height = 120;
        var camera = new Camera(width, height, AngleHelp.toRadians(90), Transforms.identity().assemble());
        assertEquals(width, camera.width);
        assertEquals(height, camera.height);
        assertEquals(0, FloatHelp.compareFloat((float)(Math.PI / 2.0), camera.fov));
        assertEquals(Matrix4.identity(), camera.transform);
    }

    @Test
    void testCameraPixelSize() {
        // width > height
        var camera = new Camera(200, 125, AngleHelp.toRadians(90), Transforms.identity().assemble());
        assertEquals(0.01f, camera.pixelSize);
    }

    @Test
    void testCameraPixelSizeA() {
        // height > width
        var camera = new Camera(125, 200, AngleHelp.toRadians(90), Transforms.identity().assemble());
        assertEquals(0.01f, camera.pixelSize);
    }

    @Test
    void testCameraRay() {
        var camera = new Camera(201, 101, AngleHelp.toRadians(90), Transforms.identity().assemble());
        var result = camera.createRay(100, 50);
        assertEquals(Tuple.makePoint(), result.origin());
        assertEquals(Tuple.makeVector(0, 0, -1), result.direction());
    }

    @Test
    void testCameraRayA() {
        var camera = new Camera(201, 101, AngleHelp.toRadians(90), Transforms.identity().assemble());
        var result = camera.createRay(0, 0);
        assertEquals(Tuple.makePoint(), result.origin());
        assertEquals(Tuple.makeVector(0.66519f, 0.33259f, -0.66851f), result.direction());
    }

    @Test
    void testCameraRayB() {
        var camera = new Camera(
            201, 101, 
            AngleHelp.toRadians(90),
            Transforms.identity().
                translate(0, -2, 5).
                rotateY(AngleHelp.toRadians(45)).
                assemble()
        );
        var result = camera.createRay(100, 50);
        assertEquals(Tuple.makePoint(0, 2, -5), result.origin());
        float sqrt2Over2 = (float) (Math.sqrt(2.0) / 2.0);
        assertEquals(Tuple.makeVector(sqrt2Over2, 0, -sqrt2Over2), result.direction());
    }

    @Test
    void testCameraRender() {
        // pretty bare bones test, render the test world and check that the center pixel is the expected color
        var light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        var sphereA = new Sphere(
            Transforms.identity().assemble(),
            Material.color(
                new Color(0.8f, 1.0f, 0.6f)).
                setDiffuse(0.7f).
                setSpecular(0.2f)
                );
                var sphereB = new Sphere(Transforms.identity().scale(0.5f, 0.5f, 0.5f).assemble());
        var world = new World();
        world.addLight(light);
        world.addShape(sphereA);
        world.addShape(sphereB);
        var camera = new Camera(11, 11, AngleHelp.toRadians(90), 
            View.makeViewMatrix(Tuple.makePoint(0, 0, -5), Tuple.makePoint(), Tuple.makeVector(0, 1, 0)));
        var result = camera.takePicture(world);
        assertEquals(new Color(0.38066f, 0.47583f, 0.2855f), result.getPixel(5, 5));
    }

}
