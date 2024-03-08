package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.matrix.Matrix4.identity;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.difference;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.intersect;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.union;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.objects.Dice;
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
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.shape.composite.CompoundShape;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class CompoundShapeDemo implements Runnable {

    private static final String fileName = "compound_shapes.ppm";
    private static final int width = 1536, height = width;
    private static final float fov = 90f;
    private final Tuple cameraPostion = makePoint(0, 3, -3);
    private final Tuple cameraTarget = makePoint(0, 0, 1.5f);
    // private final Camera camera = new TimingWrapper(width, height, fov, View.makeViewMatrix(makePoint(0, 5, 0), makePoint(0, 0, 10), makeVector(0, 1, 0)));
    private final Camera camera = new TimingWrapper(width, height, fov, View.makeViewMatrix(cameraPostion, cameraTarget, makeVector(0, 1, 0)));

    @Override
    public void run() {
        World world = new World();
        // makePoint(-2, 8, -2)
        world.addLight(new PointLight(makePoint(-2, 6.5f, -2), Colors.white));
        world.addShape( // "sky box"
            new Sphere(
                Transforms.identity().scale(15, 15, 15).assemble(),
                Material.pattern(new Perturb(
                    new BiPattern(BiOperation.radialGradient, Colors.white.multiply(0.55f), Colors.blue.multiply(0.60f), 
                    Transforms.identity().scale(0.1f, 0.1f, 0.1f).assemble()))
                )
            )
        );
        world.addShape(
            new Plane( // the floor
                Transforms.identity().assemble(),
                Material.pattern(
                    new BiPattern(
                        BiOperation.checker, 
                        new SolidColor(Colors.white.multiply(0.8f)),
                        new Perturb(new BiPattern(BiOperation.stripe, Colors.green.multiply(0.66f), Colors.blue.multiply(0.70f), 
                        Transforms.identity().rotateY(AngleHelp.toRadians(45f)).scale(0.5f, 0.5f, 0.5f).assemble()))
                    )
                ).setReflectivity(0.06f)
            )
        );
        // the dice
        world.addShape(new Dice( // RED BOTTOM
            Transforms.identity().assemble(), 
            Material.color(Colors.red.multiply(0.75f)), 
            Material.defaultMaterial()));

        world.addShape(new Dice( // GREEN BOTTOM
            Transforms.identity().rotateY(AngleHelp.toRadians(101f)).translate(-1f, 0, 0.5f).assemble(), 
            Material.color(Colors.green.multiply(0.18f)).setReflectivity(0.80f).setRefractiveIndex(1.5f).setTransparency(0.95f).setDiffuse(0.46f).setAmbient(0.06f).setShininess(500f), 
            Material.defaultMaterial()));

        world.addShape(new Dice( // BLUE TOP
            Transforms.identity().rotateX(AngleHelp.toRadians(90f)).rotateY(-10f).translate(-0.23f, 2, 1f).assemble(),
            Material.color(Colors.blue.multiply(0.75f)).setReflectivity(0.33f), 
            Material.defaultMaterial().setReflectivity(0.10f)));
        // super compound shape
        final var factor = 0.33f;
        final var mat = Material.color(Colors.blue.add(Colors.red).multiply(0.80f).add(Colors.green.multiply(0.2f))).setReflectivity(0.10f).setAmbient(0.30f);
        world.addShape(new CompoundShape(difference, 
          new CompoundShape(intersect, new Sphere(identity(), mat), new Cube(Transforms.identity().scale(0.70f).assemble(), mat), identity()),
          new CompoundShape(union,
              new CompoundShape(union, 
                    new Cylinder(Transforms.identity().scale(factor,1,factor).assemble(), mat, 1, -1, true), 
                    new Cylinder(Transforms.identity().scale(factor,1,factor).rotateZ(AngleHelp.toRadians(90f)).assemble(), mat, 1, -1, true), 
                identity()),
              new Cylinder(Transforms.identity().scale(factor,1,factor).rotateZ(AngleHelp.toRadians(90f)).rotateY(AngleHelp.toRadians(90f)).assemble(),mat, 1, -1, true), identity()), 
          Transforms.identity().scale(0.5f).rotateY(AngleHelp.toRadians(60f)).rotateZ(AngleHelp.toRadians(30f)).rotateX(AngleHelp.toRadians(-45f)).translate(1f, 3f, 0.3f).assemble()
        ));
        // pac man
        final var eyeLeftPos = new Tuple(-0.5f, 0.5f, 1f).normalize().multiply(1.2f);
        final var eyeRightPos = new Tuple(-0.5f, -0.5f, 1f).normalize().multiply(1.2f);
        final var pacManShapes = new Group(
            Transforms.identity().scale(0.66f).rotateX(AngleHelp.toRadians(-90f)).rotateZ(AngleHelp.toRadians(90f)).rotateY(AngleHelp.toRadians(-110f)).rotateZ(AngleHelp.toRadians(180f)).rotateX(AngleHelp.toRadians(33f)).rotateY(AngleHelp.toRadians(-15f)).translate(2.5f, 2.5f, -0.66f).assemble()
        );
        pacManShapes.addShape(new CompoundShape(difference,
        new CompoundShape(union, 
            new Sphere(Transforms.identity().assemble(), Material.color(Colors.red.add(Colors.green))),
            new CompoundShape(union, 
                new Sphere(Transforms.identity().scale(0.1f).translate(eyeLeftPos.x, eyeLeftPos.y, eyeLeftPos.z).assemble(), Material.color(Colors.black).setReflectivity(0.10f)),
                new Sphere(Transforms.identity().scale(0.1f).translate(eyeRightPos.x, eyeRightPos.y, eyeRightPos.z).assemble(), Material.color(Colors.black).setReflectivity(0.10f)),
                Transforms.identity().assemble()),
            Transforms.identity().assemble()),
        new Cube(Transforms.identity().shear(0, 0, 0, 0, 1, 0).translate(1.1f, 0, 2.1f).assemble(), Material.color(Colors.black).setTransparency(1).setShadow(false)), identity()));
        pacManShapes.addShape(new Sphere(Transforms.identity().scale(0.20f).translate(0.5f, 0, 1.15f).assemble(), Material.color(Colors.red.add(Colors.green))));
        world.addShape(pacManShapes);
        // The lenses
        world.addShape(new CompoundShape(intersect, 
            new Sphere(Transforms.identity().translate(0, 0, 0.5f).assemble(), Sphere.defaultGlassSphere().material()),
            new Sphere(Transforms.identity().translate(0, 0, -0.5f).assemble(), Sphere.defaultGlassSphere().material()), 
            Transforms.identity().rotateY(AngleHelp.toRadians(0f)).translate(-1, 1f, -1.8f).assemble()));
        
        world.addShape(new CompoundShape(intersect, 
            new Sphere(Transforms.identity().translate(0, 0, 0.5f).assemble(), Sphere.defaultGlassSphere().material()),
            new Sphere(Transforms.identity().translate(0, 0, -0.5f).assemble(), Sphere.defaultGlassSphere().material()), 
            Transforms.identity().scale(0.25f).rotateY(AngleHelp.toRadians(0f)).translate(cameraPostion.x - 0.1f, cameraPostion.y + 0.2f, cameraPostion.z + 0.1f).assemble()));


        // ====== take the image ========
        System.out.println("INFO: taking picture");
        var canvas = camera.takePicture(world, new ProgressWrapper(new ArrayCanvas(width, height), 20));

        System.out.println("INFO: saving image");
        var lines = CanvasWriter.canvasToPPMString(canvas);
        var file = new File(System.getProperty("user.dir"), fileName);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
        System.out.println("INFO: saved image");
        System.out.println("INFO: done");
    }
    
}
