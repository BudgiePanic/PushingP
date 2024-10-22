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
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.AreaLight;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class AreaLightDemo extends BaseDemo {

    @Override
    protected String getName() { return "soft_shadows.ppm"; }

    @Override
    protected Camera getCamera() {
        return new PinHoleCamera(1280, 720, 90f, View.makeViewMatrix(makePoint(0, 2, -5), Directions.forward.multiply(10f), Directions.up));
    }

    @Override
    protected World createWorld() {
        System.out.println("INFO: running area light demo");
        var world = new World();
        var light = new AreaLight(Colors.white.multiply(0.4f), makePoint(-3.5f, 3.5f, -3.5f), Directions.forward, Directions.up, 8, 8, AreaLight.randomSamples);
        world.addLight(new AreaLight(Colors.white.multiply(0.35f), makePoint(-1.5f, 10f, -15f), Directions.right, Directions.up, 6, 6, AreaLight.randomSamples));
        world.addLight(light);
        world.addShape(new Plane(Transforms.identity().translate(0, -0.5f, 0).assemble()));
        world.addShape(new Sphere(
            Transforms.identity().scale(2).assemble(),
            Material.color(Colors.blue.multiply(0.5f)).setReflectivity(0.7f)));
        world.addShape(new Sphere(
            Transforms.identity().translate(3, 0, -1.5f).assemble(),
            Material.color(Colors.red.multiply(0.7f)).setReflectivity(0.1f)));
        return world;
    }
    
}
