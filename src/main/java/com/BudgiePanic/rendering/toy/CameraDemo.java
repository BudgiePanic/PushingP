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

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Use the camera abstraction to painlessly take an image of an scene full of spheres.
 * 
 * @author BudgiePanic
 */
public class CameraDemo extends BaseDemo {

    @Override
    protected String getName() { return "camera_demo.ppm"; }

    @Override
    protected Camera getCamera() {
        PinHoleCamera camera = new PinHoleCamera(640, 480, 
            AngleHelp.toRadians(90), 
            View.makeViewMatrix(
                Tuple.makePoint(0, 1.5f, -5f),
                Tuple.makePoint(0, 1, 0),
                Tuple.makeVector(0, 1, 0)));
        return camera;
    }

    @Override
    protected World createWorld() {
        System.out.println("INFO: Running camera toy.");
        System.out.println("INFO: building world");
        Sphere floor = new Sphere(Transforms.identity().scale(10, 0.01f, 10).assemble(), Material.color(new Color(1, 0.9f, 0.9f)).setSpecular(0));
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
        Sphere middleSphere = new Sphere(
            Transforms.identity().translate(-0.5f, 1, 0.5f).assemble(),
            Material.color(new Color(0.1f, 1, 0.5f)).setDiffuse(0.7f).setSpecular(0.3f)
        );
        Sphere rightSphere = new Sphere(
            Transforms.identity().scale(0.5f, 0.5f, 0.5f).translate(1.5f, 0.5f, -0.5f).assemble(),
            middleSphere.material().setColor(new Color(0.5f, 1f, 0.1f))
        );
        Sphere leftSphere = new Sphere(
            Transforms.identity().scale(0.33f, 0.33f, 0.33f).translate(-1.5f, 0.33f, -0.75f).assemble(),
            middleSphere.material().setColor(new Color(1f, 0.8f, 0.1f))
        );
        PointLight light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        World world = new World();
        world.addLight(light);
        world.addShape(floor);
        world.addShape(leftWall);
        world.addShape(rightWall);
        world.addShape(middleSphere);
        world.addShape(rightSphere);
        world.addShape(leftSphere);
        return world;
    }
    
}
