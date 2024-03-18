package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Torus;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class TorusDemo extends BaseDemo {

    @Override
    protected String getName() { return "donut.ppm"; }

    @Override
    protected Camera getCamera() {
        return new Camera(1000, 1000, 70f, View.makeViewMatrix(makePoint(0, 3, -5), makePoint(), Directions.up));
    }

    @Override
    protected World createWorld() {
        World world = new World();

        world.addLight(new PointLight(makePoint(-10, 10, -5), Colors.white));
        world.addShape(new Torus(Transforms.identity().assemble(), Material.defaultMaterial(), 1f, 0.25f));

        return world;
    }
    
}
