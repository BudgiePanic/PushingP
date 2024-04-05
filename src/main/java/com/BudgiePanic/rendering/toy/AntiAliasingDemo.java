package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.DepthCamera;
import com.BudgiePanic.rendering.scene.FocusCamera;
import com.BudgiePanic.rendering.scene.NormalCamera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.SuperSamplingCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.AreaLight;
import com.BudgiePanic.rendering.util.light.Light;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Cone;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.shape.Torus;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class AntiAliasingDemo extends BaseDemo {

    @Override
    protected String getName() { return "anti_aliased.ppm"; }

    @Override
    protected Camera getCamera() {
        return new SuperSamplingCamera(
            new PinHoleCamera(1000, 1000, toRadians(70f), View.makeViewMatrix(makePoint(0, 5, -5), makePoint(0, 0, 1), Directions.up)), 
        SuperSamplingCamera.grid);
        // return new PinHoleCamera(1000, 1000, toRadians(70f), View.makeViewMatrix(makePoint(0, 5, -3), makePoint(0, 0, 1), Directions.up));
    }

    @Override
    protected World createWorld() {
        System.out.println("INFO: running anti aliasing demo scene");
        World world = new World();
        Light light = new AreaLight(Colors.white, makePoint(-10, 10, -2), Directions.forward, Directions.up, 4, 4, AreaLight.randomSamples);
        light = new PointLight(makePoint(-10, 10, -2), Colors.white);
        world.addLight(light);
        var floor = new Plane(Matrix4.identity(), Material.pattern(new BiPattern(BiOperation.checker, new SolidColor(Colors.white), new SolidColor(Colors.white.multiply(0.44f)))));
        world.addShape(floor);
        var wall = new Plane(Transforms.identity().rotateX(toRadians(90f)).translate(0, 0, 6).assemble(), 
            Material.pattern(new Perturb(new BiPattern(BiOperation.stripe, new SolidColor(Colors.red.multiply(0.8f)), new SolidColor(Colors.white.multiply(0.8f)), Transforms.identity().rotateZ(toRadians(45f)).assemble())))
        );
        world.addShape(wall);
        var donut = new Torus(Transforms.identity().rotateX(toRadians(90f)).assemble(), Material.color(Colors.green), 0.75f, 0.25f);
        world.addShape(donut);
        var sphere = new Sphere(Transforms.identity().translate(0, 3.5f, 0).assemble(), Material.defaultMaterial().setReflectivity(0.9f));
        world.addShape(sphere);
        return world;
    }
    
}
