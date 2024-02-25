package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.radialGradient;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.shape.Group;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Renders two images, one without AABB, one with AABB, measures the time taken.
 * @author BudgiePanic
 */
public class AABBDemo implements Runnable {

    private static final String fileNameA = "withoutAABB.ppm";
    private static final String fileNameB = "withAABB.ppm";
    private static final int width = 1024, height = width;
    private final static float fov = AngleHelp.toRadians(90f);
    private final Camera camera = new Camera(width, height, fov, View.makeViewMatrix(Tuple.makePoint(11f,5, 2f), Tuple.makePoint(11, 0, 8), Tuple.makePoint(0,1,0)));
    private final PointLight light = new PointLight(makePoint(-3, 10, -6), Colors.white);
    private final Plane background = new Plane(
        Transforms.identity().translate(0, -1, 0).assemble(),
        Material.pattern(new BiPattern(BiOperation.checker, Colors.green.multiply(0.66f), Colors.green.multiply(0.88f))).setSpecular(0.1f));
    private final float sqrt2 = (float) Math.sqrt(2.0);

    @Override
    public void run() {
        System.out.println("running AABB performance demo");
        System.out.println("building world with no AABB");
        var materialA = Material.defaultMaterial().setColor(Colors.red.multiply(0.33f)).setReflectivity(0.8f).setShininess(Material.defaultShininess*2);
        var materialB = Material.pattern(new Perturb(new BiPattern(radialGradient, Colors.green.multiply(0.2f), Colors.green))).setShininess(500).setReflectivity(0.5f);
        var materialC = new Material(new Color(0.2f, 0.2f, 0.2f), 0.030f, 0.5f, 0.9f, 600, 0.85f, 0.95f, 1.313f);
        // hop along 2 axis, creating clusters of spheres at each coord
        // in the AABB version, each cluster will be a group
        World world = new World();
        world.addLight(light);
        world.addShape(background);

        int offset = 5;
        for (int x = 1; x < 5; x++) {
            for (int z = 1; z < 5; z++) {
                var coord = new Pair<>(x*offset, z*offset);
                var sphereA = new Sphere(Transforms.identity().scale(1.2f, 1.2f, 1.2f).translate(coord.a(), 0, coord.b()).translate(0, 0.2f, sqrt2).assemble(), materialA); 
                var sphereB = new Sphere(Transforms.identity().translate(coord.a(), 0, coord.b()).translate(-sqrt2, 0, -sqrt2).assemble(), materialB); 
                var sphereC = new Sphere(Transforms.identity().scale(0.7f, 0.7f, 0.7f).translate(coord.a(), 0, coord.b()).translate(sqrt2, 0, -sqrt2).assemble(), materialC); 
                world.addShape(sphereA);
                world.addShape(sphereB);
                world.addShape(sphereC);
            }
        }
        System.out.println("taking picture");
        var startTime = System.currentTimeMillis();
        Canvas canvas = camera.takePicture(world);
        var endTime = System.currentTimeMillis();
        System.out.println("used " + (endTime - startTime) + " milliseconds to take picture");

        System.out.println("saving image");
        var lines = CanvasWriter.canvasToPPMString(canvas);
        File file = new File(System.getProperty("user.dir"), fileNameA);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println("saved image");
        System.out.println("building world with AABB");
        world = new World();
        world.addLight(light);
        world.addShape(background);

        // build the world
        for (int x = 1; x < 5; x++) {
            Group bigGroup = new Group(Transforms.identity().assemble());
            for (int z = 1; z < 5; z++) {
                var coord = new Pair<>(x*offset, z*offset);
                var sphereA = new Sphere(Transforms.identity().scale(1.2f, 1.2f, 1.2f).translate(0, 0.2f, sqrt2).assemble(), materialA); 
                var sphereB = new Sphere(Transforms.identity().translate(-sqrt2, 0, -sqrt2).assemble(), materialB); 
                var sphereC = new Sphere(Transforms.identity().scale(0.7f, 0.7f, 0.7f).translate(sqrt2, 0, -sqrt2).assemble(), materialC); 
                Group group = new Group(Transforms.identity().translate(coord.a(), 0, coord.b()).assemble());
                group.addShape(sphereA);
                group.addShape(sphereB);
                group.addShape(sphereC);
                bigGroup.addShape(group);
            }
            world.addShape(bigGroup);
        }
        System.out.println("taking picture");
        startTime = System.currentTimeMillis();
        canvas = camera.takePicture(world);
        endTime = System.currentTimeMillis();
        System.out.println("used " + (endTime - startTime) + " milliseconds to take picture");

        System.out.println("saving image");
        lines = CanvasWriter.canvasToPPMString(canvas);
        file = new File(System.getProperty("user.dir"), fileNameB);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println("saved image");
        System.out.println("done");
    }
    
}
