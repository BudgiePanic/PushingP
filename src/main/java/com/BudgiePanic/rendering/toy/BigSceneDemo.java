package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.WavefrontObjectLoader;
import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.SuperSamplingCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * A large scene that contains 6 Stanford dragons with over 140,000 triangles.
 * 
 * see: https://graphics.stanford.edu/data/3Dscanrep/
 * see: http://www.jamis.jamisbuck.org/bonus/assets/dragon.zip
 * see: http://www.jamis.jamisbuck.org/bonus/bounding-boxes.html
 * 
 * @author BudgiePanic
 */
public class BigSceneDemo extends BaseDemo {

    /**
     * The number of shapes that can go into a leaf node in the bounding volume hierarchy
     */
    int threshold = 30;

    double modelHeightOffset = 0.03; // not sure why our scene does not line up with the book author's scene description in the bonus chapter.// maybe we need to translate then scale? looks close enough

    @Override
    protected String getName() { return "complex_scene.ppm"; }

    @Override
    protected Camera getCamera() { /* 1200 by 480 */
        return new TimingWrapper(new SuperSamplingCamera(new PinHoleCamera(1200, 480, 1.2, View.makeViewMatrix(makePoint(0, 2.5, -10), makePoint(0, 1, 0), Directions.up)), SuperSamplingCamera.grid));
        // return new TimingWrapper(new PinHoleCamera(1200, 480, 1.2, View.makeViewMatrix(makePoint(0, 2.5, -10), makePoint(0, 1, 0), Directions.up)));
    }

    protected List<String> loadModel() {
        String fileName = "dragon.obj";
        try {
            File file = new File(fileName);
            var lines = FileUtils.readLines(file, Charset.defaultCharset());
            System.out.println("INFO: loaded model data from file successsfully");
            return lines;
        } catch (IOException e) {
            System.out.println("WARN: could not load model " + fileName);
            System.out.print("WARN: ");
            System.out.println(e);
            return List.of();
        }
    }

    @Override
    protected World createWorld() {
        World world = new World();
        world.addLight(new PointLight(makePoint(-10, 100, -100), Colors.white));
        world.addLight(new PointLight(makePoint(0, 100, 0), Colors.white.multiply(0.1)));
        world.addLight(new PointLight(makePoint(100, 10, -25), Colors.white.multiply(0.2)));
        world.addLight(new PointLight(makePoint(-100, 10, -25), Colors.white.multiply(0.2)));
        final var lines = loadModel();
        Function<Pair<Material, Matrix4>, Shape> buildDragon = (params) -> {
            final var material = params.a();
            final var transform = params.b();
            final var modelData = WavefrontObjectLoader.parseObj(lines, material);
            final var model = WavefrontObjectLoader.objectToGroup(modelData, transform);
            model.divide(threshold);
            return model; 
        };
        var boxTransform = Transforms.identity().
        translate(1, 1, 1).
        scale(3.73335, 2.5845, 1.6283).
        translate(-3.9863, -0.1217, -1.1820).
        scale(0.268).
        translate(0, modelHeightOffset, 0).
        assemble();
        var box1 = new Cube(
            boxTransform,
            Material.color(Colors.white).
            setAmbient(0).
            setDiffuse(0.4).
            setSpecular(0).
            setTransparency(0.6).
            setRefractiveIndex(1).
            setShadow(false)
        );
        var box2 = new Cube(
            boxTransform, 
            Material.color(Colors.white).
            setAmbient(0).
            setDiffuse(0.2).
            setSpecular(0).
            setTransparency(0.8).
            setRefractiveIndex(1).
            setShadow(false)
        );
        var box2A = new Cube(
            boxTransform, 
            box2.material()
        );
        var box3 = new Cube(
            boxTransform, 
            Material.color(Colors.white).
            setAmbient(0).
            setDiffuse(0.1).
            setSpecular(0).
            setTransparency(0.9).
            setRefractiveIndex(1).
            setShadow(false)
        );
        var box3A = new Cube(
            boxTransform, 
            box3.material()
        );
        
        Supplier<Shape> makeCylinder = () -> {
            return new Cylinder(Transforms.identity().translate(0, 0, 0).assemble(), 
            Material.color(Colors.white.multiply(0.2)).setAmbient(0).setDiffuse(0.8).setSpecular(0).setReflectivity(0.2),
            0, -0.15, true);
        };

        var groupA = new Group(Transforms.identity().translate(0, 2, 0).assemble());
        groupA.addShape(makeCylinder.get());
        var subGroupA = new Group(Transforms.identity().assemble());
        subGroupA.addShape(box1);
        subGroupA.addShape(buildDragon.apply(
            new Pair<Material,Matrix4>(
                Material.color(new Color(1, 0, 0.1)).setDiffuse(0.6).setSpecular(0.3).setShininess(15), 
                Transforms.identity().scale(0.268).translate(0, modelHeightOffset, 0).assemble())
            )
        );
        groupA.addShape(subGroupA);
        world.addShape(groupA);

        var groupB = new Group(Transforms.identity().translate(2, 1, -1).assemble());
        groupB.addShape(makeCylinder.get());
        var subGroupB = new Group(Transforms.identity().scale(0.75).rotateY(4).assemble());
        subGroupB.addShape(box2);
        subGroupB.addShape(buildDragon.apply(
            new Pair<Material,Matrix4>(
                Material.color(new Color(1, 0.5, 0.1)).setDiffuse(0.6).setSpecular(0.3).setShininess(15), 
                Transforms.identity().scale(0.268).translate(0, modelHeightOffset, 0).assemble())
            )
        );
        groupB.addShape(subGroupB);
        world.addShape(groupB);

        var groupC = new Group(Transforms.identity().translate(-2, 0.75, -1).assemble());
        groupC.addShape(makeCylinder.get());
        var subGroupC = new Group(Transforms.identity().scale(0.75).rotateY(-0.4).assemble());
        subGroupC.addShape(box2A);
        subGroupC.addShape(buildDragon.apply(
            new Pair<Material,Matrix4>(
                Material.color(new Color(0.9, 0.5, 0.1)).setDiffuse(0.6).setSpecular(0.3).setShininess(15), 
                Transforms.identity().scale(0.268).translate(0, modelHeightOffset, 0).assemble())
            )
        );
        groupC.addShape(subGroupC);
        world.addShape(groupC);

        var groupD = new Group(Transforms.identity().translate(-4, 0, -2).assemble());
        groupD.addShape(makeCylinder.get());
        var subGroupD = new Group(Transforms.identity().scale(0.5).rotateY(-0.2).assemble());
        subGroupD.addShape(box3);
        subGroupD.addShape(buildDragon.apply(
            new Pair<Material,Matrix4>(
                Material.color(new Color(1, 0.9, 0.1)).setDiffuse(0.6).setSpecular(0.3).setShininess(15), 
                Transforms.identity().scale(0.268).translate(0, modelHeightOffset, 0).assemble())
            )
        );
        groupD.addShape(subGroupD);
        world.addShape(groupD);

        var groupE = new Group(Transforms.identity().translate(4, 0, -2).assemble());
        groupE.addShape(makeCylinder.get());
        var subGroupE = new Group(Transforms.identity().scale(0.5).rotateY(3.3).assemble());
        subGroupE.addShape(box3A);
        subGroupE.addShape(buildDragon.apply(
            new Pair<Material,Matrix4>(
                Material.color(new Color(0.9, 1, 0.1)).setDiffuse(0.6).setSpecular(0.3).setShininess(15), 
                Transforms.identity().scale(0.268).translate(0, modelHeightOffset, 0).assemble())
            )
        );
        groupE.addShape(subGroupE);
        world.addShape(groupE);

        var groupF = new Group(Transforms.identity().translate(0, 0.5, -4).assemble());
        groupF.addShape(makeCylinder.get());
        groupF.addShape(buildDragon.apply(
            new Pair<Material,Matrix4>(
                Material.color(Colors.white).setDiffuse(0.6).setSpecular(0.3).setShininess(15), 
                Transforms.identity().scale(0.268).translate(0, modelHeightOffset, 0).rotateY(3.1415).assemble())
            )
        );
        world.addShape(groupF);

        return world;
    }
    
}
