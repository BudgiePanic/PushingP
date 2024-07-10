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
import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import java.util.List;

import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public final class PlaneDemo extends BaseDemo {

    @Override
    protected World createWorld() {
        System.out.println("running plane demo toy.");
        System.out.println("constructing world");
        final List<Shape> shapes = List.of(
            new Plane(Transforms.identity().assemble()),

            new Plane(Transforms.identity().
            rotateX(AngleHelp.toRadians(90f)).
            rotateY(AngleHelp.toRadians(30f)).
            translate(0f, 0f, 20f).assemble(),
            Material.color(Colors.blue)),

            new Sphere(Transforms.identity().scale(1.5f, 1.5f, 1.5f).assemble(),
            Material.color(Colors.blue).setShininess(350f)),

            new Sphere(Transforms.identity().shear(0.5f,3f, 1f, 1.8f, 1f, 1f).translate(0, 4, 7f).assemble(), 
            Material.color(Colors.red)),

            new Sphere(Transforms.identity().translate(3f, 3f, 0f).assemble(), Material.color(Colors.green)),

            new Sphere(Transforms.identity().translate(3.5f, 3.2f, 0f).assemble(), Material.color(Colors.white))
        );
        final List<PointLight> lights = List.of(
            new PointLight(makePoint(-2, 1.8f, -4f), Colors.white),
            new PointLight(makePoint(4, 0.5f, -2.5f), Colors.white)
        );
        World world = new World();
        world.getShapes().addAll(shapes);
        world.getLights().addAll(lights);
        return world;
    }

    @Override
    protected String getName() { return "plane_demo.ppm"; }

    @Override
    protected PinHoleCamera getCamera() {
        return new PinHoleCamera(250, 250, AngleHelp.toRadians(70f), View.makeViewMatrix(makePoint(0,3f,-10), makePoint(0,0,1), makeVector(0, 1, 0)));
    }
    
}
