package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.FocusCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.AreaLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class FocusCameraDemo extends BaseDemo {

    @Override
    protected String getName() { return "focus_camera.ppm"; }

    @Override
    protected Camera getCamera() {
        return new TimingWrapper(
            new FocusCamera(1000, 1000, 0.05f, 3f, View.makeViewMatrix(makePoint(0, 1f, -6), makePoint(0,0,1), Directions.up))
        );
        
    }

    @Override
    protected World createWorld() {
        System.out.println("INFO: running focus camera demo");
        var grey = new SolidColor(Colors.white.multiply(0.8f));
        var darkBlue = new SolidColor(Colors.blue.multiply(0.65f));
        World world = new World();
        var floor = new Plane(Transforms.identity().assemble(), 
            Material.pattern(new BiPattern(BiOperation.checker, grey, darkBlue))
        );
        world.addShape(floor);
        var walls = new Cube(Transforms.identity().scale(10f).assemble(), 
            Material.pattern(new BiPattern(BiOperation.checker, darkBlue, grey, Transforms.identity().scale(0.10f).assemble()))
        );
        world.addShape(walls);
        var numbSphere = 10;
        
        var spheres = new Group(Transforms.identity().scale(0.5f).translate(0, 0.5f, 0).assemble());
        for (int i = 0; i < numbSphere; i++) {
            var sphere = new Sphere(
                Transforms.identity().translate(-10 + i*2f, 0, -10 + i*2f).assemble(),
                Material.color(Colors.red)
            );
            spheres.addShape(sphere);
        }
        world.addShape(spheres);

        var shiny = Material.defaultMaterial().setReflectivity(0.70f);
        var shinies = new Group(Transforms.identity().assemble());
        shinies.addShape(new Sphere(Transforms.identity().scale(0.25f).translate(0,0.25f, -5).assemble(), shiny));
        shinies.addShape(new Sphere(Transforms.identity().scale(0.25f).translate(0,0.25f, -4).assemble(), shiny));
        shinies.addShape(new Sphere(Transforms.identity().scale(0.25f).translate(0,0.25f, -3).assemble(), shiny));
        shinies.addShape(new Sphere(Transforms.identity().scale(0.25f).translate(0,0.25f, -2).assemble(), shiny));
        shinies.addShape(new Sphere(Transforms.identity().scale(0.25f).translate(0,0.25f, -1).assemble(), shiny));
        shinies.addShape(new Sphere(Transforms.identity().scale(0.25f).translate(0,0.25f, 0).assemble(), shiny));
        shinies.addShape(new Sphere(Transforms.identity().scale(0.25f).translate(0,0.25f, 1).assemble(), shiny));
        world.addShape(shinies);

        world.addLight(new AreaLight(Colors.white, makePoint(-5, 5, -5), Directions.up, Directions.forward, 4, 4, AreaLight.randomSamples));
        return world;
    }
    
}
