package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class TextureMapDemo extends BaseDemo {

    @Override
    protected String getName() { return "texture_demo.ppm"; }

    @Override
    protected Camera getCamera() {
        return new PinHoleCamera(500, 500, AngleHelp.toRadians(60), View.makeViewMatrix(makePoint(0,0,-3), makePoint(0, 0, 0), Directions.up));
    }

    @Override
    protected World createWorld() {
        World world = new World();

        world.addLight(new PointLight(makePoint(-3, 5, -10), Colors.white));
        world.addShape(new Sphere(Transforms.identity().rotateY(AngleHelp.toRadians(15)).assemble(), 
            Material.pattern(new BiPattern(BiOperation.checker, Colors.white, Colors.green, Transforms.identity().scale(0.15).assemble()))
        ));

        return world;
    }
    
}