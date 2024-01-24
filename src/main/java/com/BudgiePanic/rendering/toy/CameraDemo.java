package com.BudgiePanic.rendering.toy;

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
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Use the camera abstraction to painlessly take an image of an scene full of spheres.
 * 
 * @author BudgiePanic
 */
public class CameraDemo implements Runnable {

    @Override
    public void run() {
        System.out.println("building world");
        Sphere floor = new Sphere(Transforms.identity().scale(10, 0.01f, 10).assemble(), Material.color(new Color(1, 0.9f, 0.9f)).setSpecular(0));
        Sphere leftWall = new Sphere(
            Transforms.identity().
                scale(10, 0.1f, 10).
                rotateX(AngleHelp.toRadians(90)).
                rotateY(AngleHelp.toRadians(-45)).
                translate(0, 0, 5).assemble(),
            floor.material()
        );
        Sphere rightWall = new Sphere(
            Transforms.identity().
                scale(10, 0.1f, 10).
                rotateX(AngleHelp.toRadians(90)).
                rotateY(AngleHelp.toRadians(45)).
                translate(0, 0, 5).assemble(),
            floor.material()
        );
        Sphere middleSphere = new Sphere(
            Transforms.identity().translate(-0.5f, 1, 0.5f).assemble(),
            Material.color(new Color(0.1f, 1, 0.5f)).setDiffuse(0.7f).setSpecular(0.3f)
        );
        Sphere rightSphere = new Sphere(
            Transforms.identity().scale(0.5f, 0.5f, 0.5f).translate(1.5f, 0.5f, -0.5f).assemble(),
            middleSphere.material().setColor(new Color(0.5f, 1f, 0.1f))
        );
        Sphere leftSphere = new Sphere(
            Transforms.identity().scale(0.33f, 0.33f, 0.33f).translate(-1.5f, 0.33f, -0.75f).assemble(),
            middleSphere.material().setColor(new Color(1f, 0.8f, 0.1f))
        );
        PointLight light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        World world = new World();
        world.addLight(light);
        world.addShape(floor);
        world.addShape(leftWall);
        world.addShape(rightWall);
        world.addShape(middleSphere);
        world.addShape(rightSphere);
        world.addShape(leftSphere);
        Camera camera = new Camera(640, 480, 
            AngleHelp.toRadians(90), 
            View.makeViewMatrix(
                Tuple.makePoint(0, 1.5f, -5f),
                Tuple.makePoint(0, 1, 0),
                Tuple.makeVector(0, 1, 0)));
        System.out.println("taking image");
        Canvas image = camera.takePicture(world);
        // write the image to file for viewing
        // Write the canvas to file.
        System.out.println("saving image");
        final String fileName = "camera_demo.ppm";
        var lines = CanvasWriter.canvasToPPMString(image);
        File file = new File(System.getProperty("user.dir"), fileName);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println("done");
    }
    
}
