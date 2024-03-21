package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.AreaLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Torus;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class TorusDemo extends BaseDemo {

    @Override
    protected String getName() { return "donut.ppm"; }

    @Override
    protected Camera getCamera() {
        return new PinHoleCamera(1000, 1000, 70f, View.makeViewMatrix(makePoint(0, 3, -5), makePoint(), Directions.up));
    }

    @Override
    protected World createWorld() {
        World world = new World();

        world.addLight(new AreaLight(Colors.white, makePoint(-10, 10, -5), Directions.forward, Directions.up, 6, 6, AreaLight.randomSamples));
        var donuts = new Group(Transforms.identity().translate(0, -0.2f, 0).rotateZ(AngleHelp.toRadians(-15f)).rotateX(AngleHelp.toRadians(8f)).assemble());
        donuts.addShape(new Torus(
            Transforms.identity().rotateX(AngleHelp.toRadians(90f)).translate(0, 1.5f, -1.5f).assemble(), 
            Material.defaultMaterial().setReflectivity(1).setDiffuse(0.5f),
            1f, 0.25f));
        donuts.addShape(new Torus(
            Transforms.identity().translate(-1, 1.5f, -1.5f).assemble(), 
            Material.pattern(new Perturb(new BiPattern(BiOperation.radialGradient, new SolidColor(Colors.green.add(Colors.red)), new SolidColor(Colors.blue.multiply(0.8f)), Transforms.identity().scale(0.3f).assemble()))),
            1f, 0.25f));
        world.addShape(donuts);
        world.addShape(new Plane(
            Transforms.identity().assemble(),
            Material.pattern(new BiPattern(BiOperation.checker, new SolidColor(Colors.white.multiply(0.60f)), new SolidColor(Colors.blue.multiply(0.50f))))
        ));
        world.addShape(new Plane(
            Transforms.identity().rotateX(AngleHelp.toRadians(90f)).translate(0, 0, 7).assemble(),
            Material.pattern(new BiPattern(BiOperation.checker, new SolidColor(Colors.white.multiply(0.80f)), new SolidColor(Colors.green.multiply(0.55f))))
        ));
        world.addShape(new Plane(
            Transforms.identity().rotateX(AngleHelp.toRadians(90f)).translate(0, 0, -7).assemble(),
            Material.pattern(new BiPattern(BiOperation.checker, new SolidColor(Colors.white.multiply(0.33f)), new SolidColor(Colors.red.multiply(0.60f))))
        ));

        return world;
    }
    
}
