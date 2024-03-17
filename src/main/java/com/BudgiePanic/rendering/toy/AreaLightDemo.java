package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.AreaLight;
import com.BudgiePanic.rendering.util.light.Light;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class AreaLightDemo extends BaseDemo {

    @Override
    protected String getName() { return "soft_shadows.ppm"; }

    @Override
    protected Camera getCamera() {
        return new Camera(1280, 720, 90f, View.makeViewMatrix(makePoint(0, 2, -5), makePoint(0, 0, 10), makeVector(0, 1, 0)));
    }

    @Override
    protected World createWorld() {
        System.out.println("INFO: running area light demo");
        var world = new World();
        var light = (Light) new AreaLight(Colors.white, makePoint(-10, 5, -0.5f), makeVector(0, 0, 1), makeVector(0, 1, 0), 8, 8);
        // light = new PointLight(light.position(), Colors.white);
        world.addLight(new AreaLight(Colors.white, makePoint(-1.5f, 10f, 15f), makeVector(1, 0, 0), makeVector(0, 1, 0), 8, 8));
        world.addLight(light);
        world.addShape(new Plane(Transforms.identity().translate(0, -0.5f, 0).assemble()));
        world.addShape(new Sphere(
            Transforms.identity().scale(2).assemble(),
            Material.color(Colors.blue.multiply(0.5f))));
        return world;
    }
    
}
