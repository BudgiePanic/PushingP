package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.io.WavefrontObjectLoader;
import com.BudgiePanic.rendering.reporting.ProgressWrapper;
import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class TriangleDemo implements Runnable {

    private static final String fileName = "teddy_cow_teapot.ppm";
    private static final int width = 480, height = width;
    private final static float fov = AngleHelp.toRadians(90f);
    private final Camera camera = new TimingWrapper(width, height, fov, View.makeViewMatrix(Tuple.makePoint(0,3, 0), Tuple.makePoint(0, 0, 5), Tuple.makePoint(0,1,0)));
    private final PointLight light = new PointLight(makePoint(5,10,0.5f), Colors.white);
    private final Cube background = new Cube(
        Transforms.identity().scale(15, 15, 15).translate(0, 5f, 10).assemble(),
        Material.pattern(
            new BiPattern(BiOperation.checker, Colors.white.multiply(0.66f), Colors.green.multiply(0.66f), 
                          Transforms.identity().scale(0.25f, 0.25f, 0.25f).assemble())).
            setSpecular(0.1f));
    private int modelsLoaded = 0;
    List<Matrix4> transforms = List.of(
        Transforms.identity().scale(1.5f, 1.5f, 1.5f).translate(0, -1, 5).assemble(),
        Transforms.identity().translate(3, 0, 3).assemble(),
        Transforms.identity().rotateY(AngleHelp.toRadians(90f)).scale(0.33f, 0.33f, 0.33f).translate(-4, 0, 3).assemble()
    );

    @Override
    public void run() {
        System.out.println("running triangle demo, this could take a while...");
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
                var model = WavefrontObjectLoader.objectToGroup(modelData, transforms.get(modelsLoaded));
                // The book author says you should print the size of the group to help reason about scene placement
                System.out.println("INFO: model " + fName + " extent is " + model.bounds().toString());
                models.add(model);
                System.out.println("INFO: loaded model " + fName + " successfully");
                modelsLoaded++;
            } catch (IOException e) {
                modelsLoaded++;
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
        models.forEach(world::addShape);

        // take the image
        System.out.println("INFO: taking picture, this could take a while, try removing \'Cow.obj\' or \'Teapot.obj\' from local directory to speed up the render.");
        var canvas = camera.takePicture(world, new ProgressWrapper(new ArrayCanvas(width, height), 2));

        System.out.println("saving image");
        var lines = CanvasWriter.canvasToPPMString(canvas);
        var file = new File(System.getProperty("user.dir"), fileName);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
        System.out.println("saved image");
        System.out.println("done");
    }
    
}
