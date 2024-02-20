package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.checker;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.gradient;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.radialGradient;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.ring;

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
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Cone;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class CylinderDemo implements Runnable {

    private static final String fileName = "cylinder_cone.ppm";
    private static final int width = 768, height = width;
    private final static float fov = AngleHelp.toRadians(90f);
    private final Camera camera = new Camera(width, height, fov, View.makeViewMatrix(Tuple.makePoint(0,0,-6f), Tuple.makePoint(0, 0, 1), Tuple.makePoint(0,1,0)));

    @Override
    public void run() {
        System.out.println("running cylinder demo");
        World world = new World();
        world.addLight(new PointLight(makePoint(-8, 8, -8), Colors.white));

        world.addShape(new Cylinder(Transforms.identity().shear(0.1f, 0, 0, 0, 0.2f, 0).assemble(), Material.pattern(new BiPattern(checker, new SolidColor(Colors.blue.multiply(0.8f)), new Perturb(new BiPattern(radialGradient, Colors.red, Colors.green, Transforms.identity().scale(0.15f, 0.15f, 0.15f).assemble()), 0.2f))).setShininess(500).setDiffuse(0.50f).setReflectivity(0.15f).setRefractiveIndex(1.55f).setTransparency(0.90f), 3, -3, true));
        world.addShape(new Cone(Transforms.identity().scale(2.5f, 2.5f, 2.5f).translate(-4, 1, 3f).assemble(), Material.pattern(new Perturb(new BiPattern(ring, Colors.blue, Colors.white, Transforms.identity().scale(0.2f, 0.2f, 0.2f).assemble()))).setReflectivity(0.33f), 0, -1, true));
        world.addShape(new Cube(Transforms.identity().scale(15, 15, 15).assemble(), Material.pattern(new Perturb(new BiPattern(gradient, new BiPattern(checker, Colors.white, new Color(0.66f, 0f, 0.66f)), new SolidColor(new Color(0.80f, 0.50f, 0f)), Transforms.identity().scale(0.33f, 0.33f, 0.33f).assemble()), 0.08f)).setReflectivity(0.1f).setSpecular(0.33f)), false);

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
