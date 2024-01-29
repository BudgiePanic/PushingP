package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public final class PlaneDemo implements Runnable {

    private final static String fileName = "plane_demo.ppm";
    private final static int width = 250, height = 250;
    private final static float fov = AngleHelp.toRadians(70f);
    private final Camera camera = new Camera(width, height, fov, View.makeViewMatrix(makePoint(0,3f,-10), makePoint(0,0,1), makeVector(0, 1, 0)));

    @Override
    public void run() {
        System.out.println("constructing world");
        final List<Shape> shapes = List.of(
            new Plane(Transforms.identity().assemble()),

            new Plane(Transforms.identity().
            rotateX(AngleHelp.toRadians(90f)).
            rotateY(AngleHelp.toRadians(30f)).
            translate(0f, 0f, 20f).assemble(),
            Material.color(Colors.blue)),

            new Sphere(Transforms.identity().scale(1.5f, 1.5f, 1.5f).assemble(),
            Material.color(Colors.blue).setShininess(350f)),

            new Sphere(Transforms.identity().shear(0.5f,3f, 1f, 1.8f, 1f, 1f).translate(0, 4, 7f).assemble(), 
            Material.color(Colors.red)),

            new Sphere(Transforms.identity().translate(3f, 3f, 0f).assemble(), Material.color(Colors.green)),

            new Sphere(Transforms.identity().translate(3.5f, 3.2f, 0f).assemble(), Material.color(Colors.white))
        );
        final List<PointLight> lights = List.of(
            new PointLight(makePoint(-2, 1.8f, -4f), Colors.white),
            new PointLight(makePoint(4, 0.5f, -2.5f), Colors.white)
        );
        World world = new World();
        world.getShapes().addAll(shapes);
        world.getLights().addAll(lights);
        
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
