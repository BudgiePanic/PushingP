package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.difference;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.union;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.reporting.ProgressWrapper;
import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Shape;
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
    private final Tuple cameraTarget = makePoint(0, 0, 1);
    // private final Camera camera = new TimingWrapper(width, height, fov, View.makeViewMatrix(makePoint(0, 5, 0), makePoint(0, 0, 10), makeVector(0, 1, 0)));
    private final Camera camera = new TimingWrapper(width, height, fov, View.makeViewMatrix(cameraPostion, cameraTarget, makeVector(0, 1, 0)));
    final static Matrix4 identity = Transforms.identity().assemble();

    private static Shape makeHollowSmoothCube(Matrix4 transform, Material material) {
        final var rotation = AngleHelp.toRadians(90f);
        final var max = 1;
        final var min = 0;
        // cylinders
        final var a = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateZ(-rotation).translate(0, 0, 0).assemble(), material, max, min); 
        final var b = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(1, 0, 0).rotateX(rotation).assemble(), material, max, min); 
        final var c = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(0, 0, 1).rotateZ(-rotation).assemble(), material, max, min); 
        final var d = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateX(rotation).translate(0, 0, 0).assemble(), material, max, min); 

        final var e = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(0, 0, 0).assemble(), material, max, min); 
        final var f = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(1, 0, 0).assemble(), material, max, min); 
        final var g = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(1, 0, 1).assemble(), material, max, min); 
        final var h = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(0, 0, 1).assemble(), material, max, min); 

        final var i = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateZ(-rotation).translate(0, 1, 0).assemble(), material, max, min); 
        final var j = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateX(rotation).translate(0, 1, 0).assemble(), material, max, min); 
        final var k = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateZ(-rotation).translate(0, 1, 1).assemble(), material, max, min); 
        final var l = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateX(rotation).translate(1, 1, 0).assemble(), material, max, min); 

        // cubes
        final var _1 = new Sphere(Transforms.identity().scale(0.25f).translate(0, 0, 0).assemble(), material); 
        final var _2 = new Sphere(Transforms.identity().scale(0.25f).translate(1, 0, 0).assemble(), material); 
        final var _3 = new Sphere(Transforms.identity().scale(0.25f).translate(1, 0, 1).assemble(), material); 
        final var _4 = new Sphere(Transforms.identity().scale(0.25f).translate(0, 0, 1).assemble(), material); 

        final var _5 = new Sphere(Transforms.identity().scale(0.25f).translate(0, 1, 0).assemble(), material); 
        final var _6 = new Sphere(Transforms.identity().scale(0.25f).translate(1, 1, 0).assemble(), material); 
        final var _7 = new Sphere(Transforms.identity().scale(0.25f).translate(1, 1, 1).assemble(), material); 
        final var _8 = new Sphere(Transforms.identity().scale(0.25f).translate(0, 1, 1).assemble(), material); 

        final var _1_a = new CompoundShape(union, _1, a, identity);
        final var _4_c = new CompoundShape(union, _4, c, identity);
        final var _4_cd = new CompoundShape(union, _4_c, d, identity); 
        final var _14_adc = new CompoundShape(union, _1_a, _4_cd, identity);

        final var _8_h = new CompoundShape(union, _8, h, identity);
        final var _8_jh = new CompoundShape(union, _8_h, j, identity);
        final var _5_e = new CompoundShape(union, _5, e, identity);
        final var _58_ejh = new CompoundShape(union, _5_e, _8_jh, identity);

        final var _1584_acdejh = new CompoundShape(union, _58_ejh, _14_adc, identity);

        final var _6_i = new CompoundShape(union, _6, i, identity);
        final var _6_il = new CompoundShape(union, _6_i, l, identity);
        final var _7_k = new CompoundShape(union, _7, k, identity);
        final var _67_ikl = new CompoundShape(union, _7_k, _6_il, identity);

        final var _3_g = new CompoundShape(union, _3, g, identity);
        final var _2_f = new CompoundShape(union, _2, f, identity);
        final var _2_fb = new CompoundShape(union, _2_f, b, identity);
        final var _23_fgb = new CompoundShape(union, _3_g, _2_fb, identity);

        final var _2367_fgbikl = new CompoundShape(union, _23_fgb, _67_ikl, identity);    
        
        final var _12345678_abcdefhijkl = new CompoundShape(union, _1584_acdejh, _2367_fgbikl, transform);
        return _12345678_abcdefhijkl;
    }

    private static Shape makeSmoothCube(Matrix4 transform, Material material) {
        final var vertical = new Cube(Transforms.identity().scale(0.50f,0.75f,0.5f).translate(0.5f, 0.5f, 0.5f).assemble(), material);
        final var horizontal = new Cube(Transforms.identity().scale(0.75f,0.5f,0.5f).translate(0.5f, 0.5f, 0.5f).assemble(), material);
        final var depth = new Cube(Transforms.identity().scale(0.5f,0.5f,0.75f).translate(0.5f, 0.5f, 0.5f).assemble(), material);
        final var plus = new CompoundShape(union, vertical, horizontal, identity);
        final var _3dPlus = new CompoundShape(union, plus, depth, identity);
        final var cubeHollow = makeHollowSmoothCube(identity, material);
        return new CompoundShape(union, cubeHollow, _3dPlus, transform);
    }

    private static Shape makeDice(Matrix4 transform, Material diceMaterial, Material dimpleMaterial) {
        dimpleMaterial = dimpleMaterial.setShadow(false);
        final var dimpleSize = 0.1f;
        var dimples = new Group(identity);
        
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.50f,1.0f,0.50f).assemble(), dimpleMaterial)); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0,0.25f,0.25f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0,0.75f,0.75f).assemble(), dimpleMaterial)); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0.25f,0).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.50f,0.50f,0).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0.75f,0).assemble(), dimpleMaterial)); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0.25f,1).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0.75f,1).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0.75f,1).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0.25f,1).assemble(), dimpleMaterial)); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.50f,0.5f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.25f,0.25f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.75f,0.75f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.25f,0.75f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.75f,0.25f).assemble(), dimpleMaterial)); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0,0.25f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0,0.75f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0,0.75f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0,0.25f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0,0.50f).assemble(), dimpleMaterial)); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0,0.50f).assemble(), dimpleMaterial)); 

        var cube = makeSmoothCube(Transforms.identity().translate(0.25f, 0.25f, 0.25f).scale(0.65f).assemble(), diceMaterial);
        return new CompoundShape(
            difference, 
            cube,
            dimples,
            transform);
    }

    private static Shape makeDice(Matrix4 transform, Material diceMaterial) { return makeDice(transform, diceMaterial, Material.defaultMaterial()); }

    @Override
    public void run() {
        World world = new World();
        // makePoint(-2, 8, -2)
        world.addLight(new PointLight(cameraPostion, Colors.white));
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
                )
            )
        );
        world.addShape(makeDice(Transforms.identity().scale(1.0f).rotateY(AngleHelp.toRadians(-45f)).translate(0, 0, 0).assemble(), Material.color(new Color(1,1,0).multiply(0.8f))));
        // TODO we'll come back to this once #76 is fixed...


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
