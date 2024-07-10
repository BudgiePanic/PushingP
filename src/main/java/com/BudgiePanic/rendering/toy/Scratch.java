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
package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.util.Optional;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.reporting.ProgressWrapper;
import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.BasePerspectiveCamera;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.DepthCamera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.SuperSamplingCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.raster.LineDrawer;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.shape.Torus;
import com.BudgiePanic.rendering.util.shape.composite.CompoundOperation;
import com.BudgiePanic.rendering.util.shape.composite.CompoundShape;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Demonstrates the line drawing capabilities of PushingP
 */
public class Scratch implements Runnable {
    
    protected BasePerspectiveCamera getCamera() {
        return new PinHoleCamera(1024, 1024, AngleHelp.toRadians(60), View.makeViewMatrix(makePoint(8.5,5.5,6), makePoint(0, 1, 0), Directions.up));
    }

    protected Canvas imageWorld() {
        System.out.println("INFO: taking picture");
        World world = new World();

        var light = new PointLight(makePoint(5.5, 5, 5), Colors.white);
        world.addLight(light);
        var lightShape = new Sphere(
            Transforms.identity().scale(0.05).translate(light.position().x, light.position().y, light.position().z).assemble(),
            Material.defaultMaterial().setShadow(false).setAmbient(1.0));
        world.addShape(lightShape);

        Shape s = new Sphere(Transforms.identity().assemble());
        Shape t = new Torus(Transforms.identity().assemble(), Material.defaultMaterial(), 0.85, 0.25);
        var shape = new CompoundShape(CompoundOperation.difference, s, t, Transforms.identity().assemble());
        world.addShape(shape);
        world.addShape(new Cube(
            Transforms.identity().scale(10).translate(0, 8.5, 0).assemble(), 
            Material.pattern(new BiPattern(BiOperation.checker, Colors.white.multiply(0.4).add(0.05, 0.15, 0), Colors.white.multiply(0.7), Transforms.identity().scale(0.2).assemble()))));
        
        BasePerspectiveCamera camera = getCamera();
        Camera cam = new TimingWrapper(new SuperSamplingCamera(camera, SuperSamplingCamera.defaultMode));
        var canvas = cam.takePicture(world, new ProgressWrapper(new ArrayCanvas(camera.width(), camera.height()), 20));
        var depthBuffer = Optional.of(
            new DepthCamera(camera, DepthCamera.rawUnclampedDepthValues, DepthCamera.pointDistance).takePicture(
                world, new ProgressWrapper(new ArrayCanvas(camera.width(), camera.height()), 25)
            )
        );

        LineDrawer.drawChildBoundingBoxes(LineDrawer.ALL_CHILDREN, shape, camera, canvas, new Color[]{Colors.red, Colors.white, Colors.red.add(Colors.green)}, depthBuffer, LineDrawer.ALL_SHAPES);

        LineDrawer.drawLine(light.position(), makePoint(0, 0, 0), camera, canvas, Colors.black, depthBuffer);
        var set = makePoint(0, 4, 0);
        LineDrawer.drawLine(set, set.add(1, 0, 0), camera, canvas, Colors.red, Optional.empty());  // x axis -> red
        LineDrawer.drawLine(set, set.add(0, 1, 0), camera, canvas, Colors.green, Optional.empty());// y axis -> green
        LineDrawer.drawLine(set, set.add(0, 0, 1), camera, canvas, Colors.blue, Optional.empty()); // z axis -> blue
        return canvas;
    }

    @Override
    public void run() {
        var pixels = imageWorld();
        CanvasWriter.saveImageToFile(pixels, "test.ppm");
    }
    
}
