package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class RefractionDemo implements Runnable {

    private static final String fileName = "refraction.ppm";
    private static final int width = 500, height = width;
    private final static float fov = AngleHelp.toRadians(65f);
    private final Camera camera = new Camera(width, height, fov, View.makeViewMatrix(Tuple.makePoint(0,1.5f,-6f), Tuple.makePoint(0, 0, 1), Tuple.makePoint(0,1,0)));

    /*
     * Tips from the book author on transparent and reflective materials:
     *   - glass like materials should have high transparency and reflectivity values (this causes the fresnel approximation code to activate)
     *   - reflection and refraction results add to surface color, making the object brighter. Try lowering the surface diffuse and ambient to compensate
     *     - diffuse should be inversely proportional to transparency and reflectivity
     *   - if making the surface colored, use very dark colors, near black.
     *     - the more transparent / reflective the surface, the darker the surface color should be
     *     - remember to add some diffuse and a little ambient otherwise the surface will be black
     *   - high specular and high shininess complement reflective / transparent surfaces
     */

    @Override
    public void run() {
        System.out.println("running refraction toy.");
        /*
         * A background made of 2 planes
         * A floor
         * A transparent, watery, surface
         * 3 spheres below the surface
         * A light
         */
        final var backgroundMaterial = Material.pattern(new BiPattern(
                                        BiOperation.stripe, Colors.white, new Color(0.8f, 0.8f, 0.8f),
                                        Transforms.identity().scale(2, 2, 2).assemble())).setSpecular(0.1f);
        final List<Shape> shapes = List.of(
            new Plane(Transforms.identity().translate(0, -5, 0).assemble(), Material.pattern(new BiPattern(BiOperation.ring, Colors.white, Colors.red))), // floor
            new Plane(Transforms.identity().rotateX(AngleHelp.toRadians(90f)).rotateY(AngleHelp.toRadians(3f*45f)).translate(0, 0, 10).assemble(), backgroundMaterial), // background
            new Plane(Transforms.identity().rotateX(AngleHelp.toRadians(90f)).rotateY(AngleHelp.toRadians(45f)).translate(0, 0, 10).assemble(), backgroundMaterial), // background
            // spheres
            new Sphere(Transforms.identity().assemble(), 
                Material.pattern(
                new Perturb(
                new BiPattern(BiOperation.ring, new SolidColor(Colors.red), new SolidColor(Colors.blue), 
                Transforms.identity().rotateX(2.46684f).rotateY(5.2364f).rotateZ(0.9547f).scale(0.1f, 0.1f, 0.1f).assemble())))),
            new Sphere(Transforms.identity().translate(-4, -2, 0).assemble(), Material.color(Colors.blue).setReflectivity(0.20f)),
            new Sphere(Transforms.identity().translate(4.2f, -5, 0).assemble(), 
                new Material(new Color(0.2f,0.01f,0.01f), 0.005f, 0.66f, 0.9f, 400f, 0.99f, 0.99f, 1.52f))
        );

        final List<PointLight> lights = List.of(
            new PointLight(makePoint(10f, 5f, -3.5f), Colors.white)
        );

        World world = new World();
        world.getShapes().addAll(shapes);
        world.getLights().addAll(lights);
        // add water like surface
        world.addShape(new Plane(
            Transforms.identity().assemble(), 
            new Material(new Perturb(new BiPattern(BiOperation.radialGradient, Colors.white, new Color(0.75f, 0.75f, 0.95f), Transforms.identity().scale(1.33f, 1.33f, 1.33f).assemble()),
            0.8f),
            0.005f, 0.5f, 0.9f, 300f, 0.89f, 0.92f, 1.33f)), 
            false
        );

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
