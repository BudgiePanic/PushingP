package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;
import static com.BudgiePanic.rendering.util.transform.View.makeViewMatrix;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Makes a simple scene and takes a picture of the scene from in front of the scene and another image from above the scene.
 * 
 * @author BudgiePanic
 */
public class ShadowDemo implements Runnable {

    private static final int width = 256;
    private static final int height = 128;
    private static final float fov = toRadians(90f);
    private final Camera cameraA = new Camera(width, height, fov, makeViewMatrix(makePoint(0f, 0.1f, -9), makePoint(0f, 0f, 1f), makeVector(0, 1, 0)));
    private final Camera cameraB = new Camera(width, height, fov, makeViewMatrix(makePoint(3, 2.2f, 0), makePoint(3.5f, 0, 0), makeVector(0, 1, 0)));
    private static final String cameraAImageName = "front_shadow_demo.ppm";
    private static final String cameraBImageName = "above_shadow_demo.ppm";

    @Override
    public void run() {
        // make some shadow puppets
        System.out.println("building world");
        Sphere a = new Sphere(Transforms.identity().translate(3, 0, 0).assemble());
        Sphere b = new Sphere(Transforms.identity().translate(5, 2, 0).assemble());
        Sphere c = new Sphere(Transforms.identity().translate(5, 0, 0).assemble(), a.material().setColor(Colors.green));
        Sphere d = new Sphere(Transforms.identity().scale(0.8f, 0.8f, 0.8f).translate(5.1f, -0.8f, 0).assemble(), a.material().setColor(Colors.blue));
        Sphere floor = new Sphere(Transforms.identity().scale(30, 0.3f, 30).translate(0, -12, 0).assemble(), 
        Material.color(new Color(0.7f, 0.7f, 0.7f)).setSpecular(0f));
        Sphere background = new Sphere(Transforms.identity().scale(30, 30, 0.3f).translate(0, 0, 16).assemble(), floor.material());
        
        World world = new World();
        world.addLight(new PointLight(makePoint(0,0,-5), Colors.white));
        world.addLight(new PointLight(makePoint(3,0,-5), Colors.red));
        world.addShape(a);
        world.addShape(b);
        world.addShape(c);
        world.addShape(d);
        world.addShape(floor);
        world.addShape(background);

        System.out.println(String.format("taking first picture \"%s\"", cameraAImageName));
        Canvas imageA = cameraA.takePicture(world);
        System.out.println(String.format("taking second picture \"%s\"", cameraBImageName));
        Canvas imageB = cameraB.takePicture(world);

        System.out.println("saving images...");
        var linesA = CanvasWriter.canvasToPPMString(imageA);
        var linesB = CanvasWriter.canvasToPPMString(imageB);

        File fileA = new File(System.getProperty("user.dir"), cameraAImageName);
        File fileB = new File(System.getProperty("user.dir"), cameraBImageName);
        try {
            FileUtils.writeLines(fileA, linesA);
            FileUtils.writeLines(fileB, linesB);
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println("done");
    }
    
}