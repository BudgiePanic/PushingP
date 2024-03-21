package com.BudgiePanic.rendering.toy;

import java.util.List;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

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
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class CubeDemo extends BaseDemo { 

    @Override
    protected World createWorld() {
        System.out.println("INFO: Running cube demo");
        World world = new World();

        final var small = Transforms.identity().scale(0.2f, 0.2f, 0.2f).assemble();
        final var angle = AngleHelp.toRadians(45f); 

        final List<PointLight> lights = List.of(new PointLight(makePoint(5, 8, -5), Colors.white));
        world.getLights().addAll(lights);

        var shape = new Cube(
            Transforms.identity().scale(1.5f, 0.05f, 0.75f).rotateX(angle).translate(2, 2, 0).assemble(),
            new Material(new Perturb(new BiPattern(BiOperation.gradient, Colors.white, new Color(0.8f,0.8f,0.8f))),
            0.008f, 0.46f, 0.9f, 400f, 0.9f, 0.95f, 1.33f, false));
        world.addShape(shape);

        shape = new Cube(
            Transforms.identity().scale(10, 10, 10).assemble(),
            Material.color(Colors.blue.add(0, 0.8f, 0).multiply(0.33f)).
            setReflectivity(0.35f).setShininess(600).setSpecular(0.6f));
        world.addShape(shape);
        
        shape = new Cube(
            Transforms.identity().rotateX(angle).rotateY(angle).rotateZ(angle).assemble(),
            Material.pattern(new Perturb(new BiPattern(BiOperation.radialGradient, Colors.red, Colors.white, small))).setAmbient(0.4f));
        world.addShape(shape);

        shape = new Cube(
            Transforms.identity().rotateY(angle).translate(2.5f, 2.5f, 3.5f).assemble(),
            Material.pattern(new BiPattern(BiOperation.stripe, Colors.green, Colors.white, Transforms.identity().scale(0.15f, 0.15f, 0.15f).assemble())));
        world.addShape(shape);

        return world;
    }

    @Override
    protected String getName() { return "cube.ppm"; }

    @Override
    protected Camera getCamera() {
        return new PinHoleCamera(500, 500, AngleHelp.toRadians(70f), View.makeViewMatrix(Tuple.makePoint(0,1.5f,-6f), Tuple.makePoint(0, 0, 1), Tuple.makePoint(0,1,0)));
    }
    
}
