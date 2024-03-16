package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.WavefrontObjectLoader;
import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class TriangleDemo extends BaseDemo {

    private final PointLight light = new PointLight(makePoint(5,10,0.5f), Colors.white);
    private final Cube background = new Cube(
        Transforms.identity().scale(15, 15, 15).translate(0, 5f, 10).assemble(),
        Material.pattern(
            new BiPattern(BiOperation.checker, Colors.white.multiply(0.66f), Colors.green.multiply(0.66f), 
                          Transforms.identity().scale(0.25f, 0.25f, 0.25f).assemble())).
            setSpecular(0.1f));
    private final Plane floor = new Plane(Transforms.identity().translate(0, -7, 0).assemble(),
        Material.pattern(new BiPattern(BiOperation.checker, Colors.white.multiply(0.75f), Colors.red.multiply(0.50f), 
        Transforms.identity().scale(3f, 3f, 3f).shear(1, 0, 0, 0, 0, 0).rotateY(AngleHelp.toRadians(45f)).assemble())).setSpecular(0.3f));
    Map<String, Matrix4> transforms = Map.ofEntries(
        Map.entry("Teapot.obj", Transforms.identity().scale(1.5f, 1.5f, 1.5f).translate(0, -1, 9).assemble()),
        Map.entry("Cow.obj", Transforms.identity().rotateY(AngleHelp.toRadians(110f)).scale(0.70f, 0.70f, 0.70f).translate(7, -3, 8.5f).assemble()),
        Map.entry("Teddy_bear.obj", Transforms.identity().rotateY(AngleHelp.toRadians(120f)).scale(0.12f, 0.12f, 0.12f).translate(-4, 0, 4).assemble())
    );

    @Override
    protected World createWorld() {
        System.out.println("INFO: running triangle demo, this could take a while...");
        // load the object files from local directory
        List<Shape> models = new ArrayList<>();
        System.out.println("INFO: attempting to load models");
        System.out.println("INFO: trying to load models with " + Charset.defaultCharset() + " charset");
        for (String fName : List.of("Teapot.obj", "Cow.obj", "Teddy_bear.obj")) {
            System.out.println("INFO: looking for model " + fName + " in " + System.getProperty("user.dir"));
            try {
                File file = new File(fName);
                var lines = FileUtils.readLines(file, Charset.defaultCharset());
                var modelData = WavefrontObjectLoader.parseObj(lines, Material.defaultMaterial().setReflectivity(0.2f));
                System.out.println("INFO: model has " + modelData.triangles().size() + " triangles");
                System.out.println("INFO: model has " + modelData.vertices().size() + " verticies");
                var model = WavefrontObjectLoader.objectToGroup(modelData, transforms.get(fName));
                // The book author says you should print the size of the group to help reason about scene placement
                System.out.println("INFO: model " + fName + " local extent is " + model.bounds().toString());
                models.add(model);
                System.out.println("INFO: loaded model " + fName + " successfully");
            } catch (IOException e) {
                System.out.println("WARN: could not load model " + fName);
                System.out.print("WARN: ");
                System.out.println(e);
                continue;
            }
        }

        // build the scene
        final World world = new World();
        world.addLight(light);
        world.addShape(background);
        world.addShape(floor);
        models.forEach(world::addShape);

        return world;
    }

    @Override
    protected String getName() { return "teddy_cow_teapot.ppm"; }

    @Override
    protected Camera getCamera() {
        return new TimingWrapper(480, 480, AngleHelp.toRadians(90f), View.makeViewMatrix(Tuple.makePoint(0,3, 0), Tuple.makePoint(0, 0, 5), Tuple.makePoint(0,1,0)));
    }
    
}
