package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.AreaLight;
import com.BudgiePanic.rendering.util.light.DirectionLight;
import com.BudgiePanic.rendering.util.light.Light;
import com.BudgiePanic.rendering.util.light.PointSpotLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class SpotLightDemo extends BaseDemo {

    @Override
    protected String getName() { return "spotlight.ppm"; }

    @Override
    protected Camera getCamera() { return new PinHoleCamera(1280, 768, toRadians(90f), View.makeViewMatrix(makePoint(0, 20, 0), makePoint(0f, 0f, 0f), Directions.left)); }

    @Override
    protected World createWorld() {
        System.out.println("INFO: running spotligt lighting demo");
        World world = new World();
        var lightPosition = makePoint(10, 8, 0);
        Light light = new PointSpotLight(lightPosition, Colors.white, makePoint().subtract(lightPosition), toRadians(10f), toRadians(45f));
        // light = new DirectionLight(new AreaLight(Colors.white, lightPosition, Directions.forward, Directions.right, 3, 3, AreaLight.randomSamples), toRadians(45), true);
        world.addLight(light);
        world.addShape(new Cube(Transforms.identity().translate(-6, 1.4f, 5f).assemble(), Material.color(Colors.red)));
        world.addShape(new Sphere(Transforms.identity().translate(0, 1f, 0f).assemble(), Material.color(Colors.red).setReflectivity(0.4f)));
        world.addShape(new Plane(Transforms.identity().assemble(), Material.pattern(new BiPattern(BiOperation.checker, new SolidColor(Colors.green), new SolidColor(Colors.white)))));
        return world;
    }
    
}