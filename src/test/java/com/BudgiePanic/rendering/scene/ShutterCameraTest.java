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

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.reporting.ProceduralCameraWrapper;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.BoundingBox;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.LinearMotionShape;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Tests for verifying that motion blur artifacts are produced my motion shapes and the shutter camera.
 */
public class ShutterCameraTest {

    @Test
    void testMovingShapeA() {
        // check that a pixel that is only intersecting with a moving shape for part of an exposure returns a different color
        World world = new World();
        world.addLight(new PointLight(makePoint(0, 0, 0), Colors.white));
        world.addShape(new LinearMotionShape(Matrix4.identity(), new Cube(Transforms.identity().scale(0.25f).assemble(), Material.defaultMaterial().setAmbient(1).setDiffuse(0).setSpecular(0)), Directions.left));
        Camera camera = new ProceduralCameraWrapper(new ShutterCamera(
            new PinHoleCamera(1, 1, 10f, View.makeViewMatrix(makePoint(0, 0, -5), makePoint(0, 0, 1), Directions.up)),
            1, 2, ShutterCamera.averaged));
        var canvas = camera.takePicture(world);
        assertEquals(new Color(0.707106f,0.707106f,0.707106f), canvas.getPixel(0, 0));
    }

    @Test
    void testMovingShapeB() {
        World world = new World();
        Shape shape = new LinearMotionShape(Transforms.identity().assemble(), new Sphere(Transforms.identity().translate(0, 1, 0).assemble()), Directions.right.multiply(100));
        world.addShape(shape);
        world.addLight(new PointLight(makePoint(-5, 15, -8), Colors.white));
        Camera camera = new ShutterCamera(
            new PinHoleCamera(
            768, 768, AngleHelp.toRadians(40f), View.makeViewMatrix(makePoint(0, 10, -6), makePoint(0, 0, 1), Directions.up)),
        ShutterCamera.defaultExposureTime, 1, ShutterCamera.defaultExposureMode);
        var result = camera.pixelAt(world, 380 + 0.5f, 353 + 0.5f, 0f);
        assertNotEquals(Colors.black, result);
    }

    @Test
    void testCameraBakesExposureTime() {
        // the user needs to tell the world what exposure the shutter camera is using, so the motion shapes can create their AABB's
        // if the user doesn't tell the world, then motion shapes will not use their AABBs making the render slower, especially if the child shape is very complex to perform 
        // intersection tests against
        World world = new World();
        Shape shape = new LinearMotionShape(Transforms.identity().assemble(), new Sphere(Transforms.identity().assemble()), Directions.right);
        world.addShape(shape);
        var camera = new ShutterCamera(new PinHoleCamera(1, 1, 1f, Matrix4.identity()), 5f, 1, ShutterCamera.defaultExposureMode);
        world.bakeEndTime(camera.exposureDuration);
        var expected = new BoundingBox(makePoint(-1, -1, -1), makePoint(6, 1, 1));
        var result = shape.bounds();
        assertEquals(expected, result);
    }
}
