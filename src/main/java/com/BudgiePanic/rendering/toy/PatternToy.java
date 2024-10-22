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
import static com.BudgiePanic.rendering.util.pattern.BiOperation.blend;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.radialGradient;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.ring;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.stripe;

import java.util.List;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class PatternToy extends BaseDemo {

    @Override
    protected World createWorld() {
        System.out.println("INFO: running pattern toy");
        System.out.println("INFO: building world");
        final List<Shape> shapes = List.of(
            new Plane(Transforms.identity().assemble(), 
            Material.pattern(
                new Perturb(
                    new BiPattern(BiOperation.radialGradient, 
                        new BiPattern(BiOperation.checker, new SolidColor(Colors.green), new SolidColor(Colors.blue)),
                        new BiPattern(BiOperation.ring, new SolidColor(Colors.white), new SolidColor(Colors.red)))
                    ))),
            
            new Sphere(
                Transforms.identity().translate(-4, 2f, 1).assemble(), 
                Material.pattern(
                    new Perturb(new Perturb(new Perturb(
                        new BiPattern(BiOperation.ring, Colors.blue, Colors.white, Transforms.identity().scale(0.25f, 0.25f, 0.25f).assemble()), 0.1f),
                        0.15f),0.25f)
                    )),

            new Sphere(
                Transforms.identity().translate(4, 2f, 1).assemble(),
                Material.pattern(new BiPattern(blend, 
                        new BiPattern(stripe, new Color(0.33f, 0.33f, 1), new Color(0.5f,0.5f,0.5f)),
                        new BiPattern(stripe, Colors.white, new Color(0.5f, 1, 0.33f), 
                            Transforms.identity().rotateZ(AngleHelp.toRadians(90f)).assemble()),
                    Transforms.identity().scale(0.33f, 0.33f, 0.33f).assemble()))),

            new Sphere(Transforms.identity().translate(0, 3, 1).assemble(),
                Material.pattern(new BiPattern(stripe,
                    new Perturb(new BiPattern(ring, new Color(0.80f, 0.80f, 0.01f), new Color(0.01f, 0.90f, 0.23f), 
                        Transforms.identity().scale(1f, 0.2f, 1f).assemble()), 0.05f),
                    new BiPattern(radialGradient, new Color(0f, 0.98f, 0.6f), new Color(0.92f, 0.55f, 0.08f)),
                    Transforms.identity().scale(0.25f, 0.25f, 0.25f).assemble())))
        );

        final List<PointLight> lights = List.of(
            new PointLight(makePoint(3, 5, -2), Colors.white)
        );

        World world = new World();
        world.getShapes().addAll(shapes);
        world.getLights().addAll(lights);
        return world;
    }

    @Override
    protected String getName() { return "pattern_demo.ppm"; }

    @Override
    protected Camera getCamera() {
        return new PinHoleCamera(700, 700, AngleHelp.toRadians(65f), View.makeViewMatrix(Tuple.makePoint(0,5,-4.5f), Tuple.makePoint(0, 1, 1), Tuple.makePoint(0,1,0)));
    }
    
}
