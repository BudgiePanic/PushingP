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

import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class TestScene extends BaseDemo {

    @Override
    protected World createWorld() {
        System.out.println("INFO: running test scene, check for acne artefacts in the resulting image");
        final var materialA = Material.pattern(new BiPattern(BiOperation.checker, Colors.white, Colors.black));
        final var materialB = Material.pattern(new BiPattern(BiOperation.checker, Colors.green, Colors.blue));
        final var materialC = Material.pattern(new BiPattern(BiOperation.gradient, Colors.red, Colors.blue));
        World world = new World();
        world.addLight(new PointLight(makePoint(-8, 8, -8), Colors.white));
        world.addShape(new Plane(Transforms.identity().rotateX(toRadians(90f)).translate(0, 0, 6).assemble(), materialA));
        world.addShape(new Cube(Transforms.identity().rotateX(toRadians(90f)).rotateY(toRadians(45f)).rotateZ(toRadians(360f)).assemble(), materialB));
        world.addShape(new Sphere(Transforms.identity().translate(0, 4.5f, 0).assemble(), materialC));
        return world;
    }

    @Override
    protected String getName() { return "test_scene.ppm"; }

    @Override
    protected Camera getCamera() { return new PinHoleCamera(512, 512, AngleHelp.toRadians(90f), View.makeViewMatrix(Tuple.makePoint(0,0,-6f), Tuple.makePoint(0, 0, 1), Tuple.makePoint(0,1,0))); }
    
}
