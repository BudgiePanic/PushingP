package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.io.File;
import java.io.IOException;

import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class TestScene implements Runnable {

    private static final String fileName = "test_scene.ppm";
    private static final int width = 512, height = width;
    private final static float fov = AngleHelp.toRadians(90f);
    private final Camera camera = new Camera(width, height, fov, View.makeViewMatrix(Tuple.makePoint(0,0,-6f), Tuple.makePoint(0, 0, 1), Tuple.makePoint(0,1,0)));

    @Override
    public void run() {
        System.out.println("running test scene, check for acne artefacts in the resulting image");
        final var materialA = Material.pattern(new BiPattern(BiOperation.checker, Colors.white, Colors.black));
        final var materialB = Material.pattern(new BiPattern(BiOperation.checker, Colors.green, Colors.blue));
        final var materialC = Material.pattern(new BiPattern(BiOperation.gradient, Colors.red, Colors.blue));
        World world = new World();
        world.addLight(new PointLight(makePoint(-8, 8, -8), Colors.white));
        world.addShape(new Plane(Transforms.identity().rotateX(toRadians(90f)).translate(0, 0, 6).assemble(), materialA));
        world.addShape(new Cube(Transforms.identity().rotateX(toRadians(90f)).rotateY(toRadians(45f)).rotateZ(toRadians(360f)).assemble(), materialB));
        world.addShape(new Sphere(Transforms.identity().translate(0, 4.5f, 0).assemble(), materialC));
        System.out.println("taking image");
        Canvas canvas = camera.takePicture(world);

        System.out.println("saving image");
        var lines = CanvasWriter.canvasToPPMString(canvas);
        File file = new File(System.getProperty("user.dir"), fileName);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println("done");
    }
    
}
