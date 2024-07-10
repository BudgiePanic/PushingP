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

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.ShutterCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.LinearMotionShape;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class MotionDemo extends BaseDemo {

    @Override
    protected String getName() { return "motion_blur.ppm"; }

    @Override
    protected Camera getCamera() {
        return new ShutterCamera(
            new PinHoleCamera(
            768, 768, AngleHelp.toRadians(40f), View.makeViewMatrix(makePoint(0, 10, -6), makePoint(0, 0, 1), Directions.up)),
        ShutterCamera.defaultExposureTime, ShutterCamera.defaultRaysPerExposure, ShutterCamera.defaultExposureMode);
    }

    @Override
    protected World createWorld() {
        System.out.println("INFO: running motion blur demo");
        World world = new World();
        // floor
        world.addShape(new Plane(Transforms.identity().assemble(), Material.pattern(new BiPattern(BiOperation.checker, new SolidColor(new Color(1, 0.624f, 0)), new SolidColor(new Color(1f,0.82f,0.518f))))));
        // reflective back mirror
        world.addShape(new Cube(Transforms.identity().scale(3, 3, 0.1f).rotateY(AngleHelp.toRadians(-15f)).translate(0, 1f, 2.5f).assemble(), Material.defaultMaterial().setReflectivity(1f).setSpecular(0).setAmbient(0.001f).setDiffuse(0.5f)));
        // stationary object
        world.addShape(new Cylinder(Transforms.identity().translate(-5, 0, -1).assemble(), Material.pattern(new BiPattern(BiOperation.checker, new SolidColor(new Color(1, 0, 0.592f)), new SolidColor(new Color(0.91f,0.573f,0.773f)))), 2.5f, 0, true));
        // moving object
        world.addShape(new LinearMotionShape(Transforms.identity().assemble(), new Sphere(Transforms.identity().translate(0, 1, 0).assemble(), Material.color(new Color(0.949f,0.259f,0.259f))), Directions.right.multiply(20)));
        // light source
        world.addLight(new PointLight(makePoint(-5, 15, -8), Colors.white));
        // bake scene
        world.bakeEndTime(ShutterCamera.defaultExposureTime);
        return world;
    }
    
}
