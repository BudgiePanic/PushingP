package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.checker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.reporting.ProgressWrapper;
import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.noise.Perlin;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.Perturb;
import com.BudgiePanic.rendering.util.pattern.SolidColor;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.Group;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class GroupDemo implements Runnable {

    private static final String fileName = "group.ppm";
    private static final int width = 768, height = width;
    private final static float fov = AngleHelp.toRadians(90f);
    private final Tuple cameraLocation = Tuple.makePoint(3,0,-3);
    private final Camera camera = new TimingWrapper(width, height, fov, View.makeViewMatrix(cameraLocation, Tuple.makePoint(3, 0, 0), Tuple.makePoint(0,1,0)));
    private final List<Material> materials = List.of(
            Material.defaultMaterial(),
            Material.pattern(new Perturb(new BiPattern(BiOperation.radialGradient, 
                new BiPattern(BiOperation.checker, new SolidColor(Colors.green), new SolidColor(Colors.blue)),
                new BiPattern(BiOperation.ring, new SolidColor(Colors.white), new SolidColor(Colors.red)))
            )).setReflectivity(0.5f),
            Material.pattern(new Perturb(new Perturb(new Perturb(
                new BiPattern(BiOperation.ring, Colors.blue, Colors.white, Transforms.identity().scale(0.25f, 0.25f, 0.25f).assemble()), 0.1f),
                0.15f),0.25f)
            ),
            Material.pattern(new BiPattern(BiOperation.ring, Colors.white, Colors.red)),
            new Material(new Color(0.25f,0.01f,0.01f), 0.005f, 0.66f, 0.9f, 600f, 0.99f, 0.99f, 1.52f),
            new Material(new Color(0.1f,0.1f,0.1f), 0.005f, 0.55f, 0.9f, 400f, 0.85f, 0.90f, 1.2f)
    );
    private final Random random = new Random(65894513251L);
    private final float pi2 = (float) (Math.PI * 2.0);
    private final Plane background = new Plane(
        Transforms.identity().rotateX(AngleHelp.toRadians(90f)).translate(0, 0, 15).assemble(), 
        Material.pattern(new BiPattern(checker, Colors.white, Colors.white.multiply(0.7f))).setAmbient(1).setDiffuse(0).setSpecular(0));

    private Shape buildHexagonCorner(Material material) {
        return new Sphere(Transforms.identity().scale(0.25f, 0.25f, 0.25f).translate(0, 0, -1).assemble(), material);
    }

    private Shape buildHexagonEdge(Material material) {
        return new Cylinder(
            Transforms.identity().scale(0.25f, 1, 0.25f).rotateZ(AngleHelp.toRadians(-90f)).rotateY(AngleHelp.toRadians(-30f)).translate(0, 0, -1).assemble(), 
            material,
            1, 0);
    }

    private Shape buildHexagonSide(Matrix4 transform, Material material) {
        var side = new Group(transform);
        side.addShape(buildHexagonCorner(material));
        side.addShape(buildHexagonEdge(material));
        return side;
    }

    private Shape buildHexagon(Matrix4 transform, Material material) {
        var hexagon = new Group(transform);
        for (int i = 0; i < 6; i++) {
            var side = buildHexagonSide(Transforms.identity().rotateY(i * AngleHelp.toRadians(60f)).assemble(), material);
            hexagon.addShape(side);
        }
        return hexagon;
    }

    private Material randomMaterial() { return materials.get(random.nextInt(materials.size())); }

    @Override
    public void run() {
        // generate 13 hexagons of varying materials within 8 AABB containers
        System.out.println("Running groups demo");
        System.out.println("building world");
        World world = new World();

        world.addLight(new PointLight(cameraLocation, Colors.white));
        world.addShape(background);
        
        int limit = 6;
        for (int x = 0; x < limit; x++) {
            for (int y = 0; y < limit; y++) {
                var group = new Group(Transforms.identity().assemble());
                for (int z = 0; z < limit; z++) {
                    var translation = new Tuple(x, y, z);
                    var noiseA = Perlin.noise(0.1f * x, 0.1f * y, 0.1f * z);
                    var noiseB = Perlin.noise(0.1f * x, 0.1f * y, 0.1f * z + 1);
                    var noiseC = Perlin.noise(0.1f * x, 0.1f * y, 0.1f * z + 2);
                    var scale = 0.33f;
                    var transform = Transforms.identity().scale(scale, scale, scale).
                        rotateX(pi2 * noiseA).rotateY(pi2 * noiseB).rotateZ(pi2 * noiseC).
                        translate(translation.x, translation.y, translation.z).assemble();
                    var hexagon = buildHexagon(transform, randomMaterial());
                    group.addShape(hexagon);
                }
                world.addShape(group);
            }
        }

        System.out.println("taking image");
        Canvas canvas = camera.takePicture(world, new ProgressWrapper(new ArrayCanvas(width, height)));

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
