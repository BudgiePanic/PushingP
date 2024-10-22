/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.scene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

/**
 * Tests for the pinhole camera
 */
public class PinHoleCameraTest {
    
    @Test
    void testCameraConstructor() {
        var width = 160;
        var height = 120;
        var camera = new PinHoleCamera(width, height, AngleHelp.toRadians(90.0), Transforms.identity().assemble());
        assertEquals(width, camera.width);
        assertEquals(height, camera.height);
        assertEquals(0, FloatHelp.compareFloat((Math.PI / 2.0), camera.fov));
        assertEquals(Matrix4.identity(), camera.transform);
    }

    @Test
    void testCameraPixelSize() {
        // width > height
        var camera = new PinHoleCamera(200, 125, AngleHelp.toRadians(90.0), Transforms.identity().assemble());
        assertTrue(FloatHelp.compareFloat(0.01, camera.pixelSize) == 0, "expected 0.01 actual " + camera.pixelSize);
        // assertEquals(0.01, camera.pixelSize);
    }

    @Test
    void testCameraPixelSizeA() {
        // height > width
        var camera = new PinHoleCamera(125, 200, AngleHelp.toRadians(90.0), Transforms.identity().assemble());
        assertTrue(FloatHelp.compareFloat(0.01, camera.pixelSize) == 0, "expected 0.01 actual " + camera.pixelSize);
        // assertEquals(0.01, camera.pixelSize);
    }

    @Test
    void testCameraRay() {
        var camera = new PinHoleCamera(201, 101, AngleHelp.toRadians(90.0), Transforms.identity().assemble());
        var result = camera.createRay(100 + 0.5, 50 + 0.5, 0);
        assertEquals(Tuple.makePoint(), result.origin());
        assertEquals(Tuple.makeVector(0, 0, -1), result.direction());
    }

    @Test
    void testCameraRayA() {
        var camera = new PinHoleCamera(201, 101, AngleHelp.toRadians(90.0), Transforms.identity().assemble());
        var result = camera.createRay(0 + 0.5, 0 + 0.5, 0);
        assertEquals(Tuple.makePoint(), result.origin());
        assertEquals(Tuple.makeVector(0.66519, 0.33259, -0.66851), result.direction());
    }

    @Test
    void testCameraRayB() {
        var camera = new PinHoleCamera(
            201, 101, 
            AngleHelp.toRadians(90.0),
            Transforms.identity().
                translate(0, -2, 5).
                rotateY(AngleHelp.toRadians(45.0)).
                assemble()
        );
        var result = camera.createRay(100 + 0.5, 50 + 0.5, 0);
        assertEquals(Tuple.makePoint(0, 2, -5), result.origin());
        double sqrt2Over2 = (Math.sqrt(2.0) / 2.0);
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
        var camera = new PinHoleCamera(11, 11, AngleHelp.toRadians(90), 
            View.makeViewMatrix(Tuple.makePoint(0, 0, -5), Tuple.makePoint(), Tuple.makeVector(0, 1, 0)));
        var result = camera.takePicture(world);
        assertEquals(new Color(0.38066f, 0.47583f, 0.2855f), result.getPixel(5, 5));
    }

}
