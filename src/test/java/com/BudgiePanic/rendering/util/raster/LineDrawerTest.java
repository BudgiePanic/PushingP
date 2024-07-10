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
package com.BudgiePanic.rendering.util.raster;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.scene.BasePerspectiveCamera;
import com.BudgiePanic.rendering.scene.DepthCamera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.light.Light;
import com.BudgiePanic.rendering.util.shape.Cone;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Manual tests for end to end 3D line segment to 2D line rasterization onto canvas.
 */
public class LineDrawerTest {
    @Test
    void testDrawLine() {
        // Testing the depth draw line - Points are clipped because they are behind the near plane
        World world = new World();
        Shape shape = new Cube(Transforms.identity().translate(0, 0, -3).assemble());
        world.addShape(shape);
        Light light = new PointLight(makePoint(0, 5, 0), Colors.white);
        world.addLight(light);
        BasePerspectiveCamera camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0, 0, -1), Directions.up));
        Canvas depthBuffer = new DepthCamera(camera, DepthCamera.rawUnclampedDepthValues, DepthCamera.pointDistance).takePicture(world);
        Canvas dummy = camera.takePicture(world);
        LineDrawer.drawLine(makePoint(0, -3, 0), makePoint(0, 3, 0), camera, dummy, Colors.green, Optional.of(depthBuffer));
    }

    @Test
    void testDrawLineA() {
        // Testing the depth draw line, points are exactly on the near clipping plane - they get filtered
        World world = new World();
        Shape shape = new Cube(Transforms.identity().translate(0, 0, -3).assemble());
        world.addShape(shape);
        Light light = new PointLight(makePoint(0, 5, 0), Colors.white);
        world.addLight(light);
        BasePerspectiveCamera camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0, 0, -1), Directions.up));
        Canvas depthBuffer = new DepthCamera(camera, DepthCamera.rawUnclampedDepthValues, DepthCamera.pointDistance).takePicture(world);
        Canvas dummy = camera.takePicture(world);
        LineDrawer.drawLine(makePoint(0, -3, -1), makePoint(0, 3, -1), camera, dummy, Colors.green, Optional.of(depthBuffer));
    }

    @Test
    void testDrawLineB() {
        // Testing the depth draw line, points are just in front of the near clipping plane
        World world = new World();
        Shape shape = new Cube(Transforms.identity().translate(0, 0, -3).assemble());
        world.addShape(shape);
        Light light = new PointLight(makePoint(0, 5, 0), Colors.white);
        world.addLight(light);
        BasePerspectiveCamera camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0, 0, -1), Directions.up));
        Canvas depthBuffer = new DepthCamera(camera, DepthCamera.rawUnclampedDepthValues, DepthCamera.pointDistance).takePicture(world);
        Canvas dummy = camera.takePicture(world);
        LineDrawer.drawLine(makePoint(0, -3, -1.01), makePoint(0, 3, -1.01), camera, dummy, Colors.green, Optional.of(depthBuffer));
    }

    @Test
    void testDrawLineC() {
        // Testing the depth draw line
        World world = new World();
        Shape shape = new Cube(Transforms.identity().translate(0, 0, -3).assemble());
        world.addShape(shape);
        Light light = new PointLight(makePoint(0, 5, 0), Colors.white);
        world.addLight(light);
        BasePerspectiveCamera camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0, 0, -1), Directions.up));
        Canvas depthBuffer = new DepthCamera(camera, DepthCamera.rawUnclampedDepthValues, DepthCamera.pointDistance).takePicture(world);
        Canvas dummy = camera.takePicture(world);
        LineDrawer.drawLine(makePoint(0, -3, -3), makePoint(0, 3, -3), camera, dummy, Colors.green, Optional.of(depthBuffer));
    }

    @Test
    void testDrawLineEdge() {
        // test draw line method edge conditions, infinite bounding box
        assertDoesNotThrow(() -> {
            BasePerspectiveCamera camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0, 0, -1), Directions.up));
            Shape shape = new Cone(Transforms.identity().translate(0, 0, -5).assemble());
            Canvas canvas = new ArrayCanvas(camera.width(), camera.height());
            LineDrawer.drawBoundingBox(shape, camera, canvas, Colors.white, Optional.empty());
        });
    }

    @Test
    void testDrawLineEdgeA() {
        assertDoesNotThrow(()->{
            BasePerspectiveCamera camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0, 0, -1), Directions.up));
            Canvas canvas = new ArrayCanvas(camera.width(), camera.height());
            LineDrawer.drawLine(makePoint(0, Double.POSITIVE_INFINITY, -2), makePoint(0, Double.NEGATIVE_INFINITY, -2), camera, canvas, Colors.white, Optional.empty());
        });
    }
}
