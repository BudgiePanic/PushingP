package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.WavefrontObjectLoader;
import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.SuperSamplingCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
// import com.BudgiePanic.rendering.util.light.AreaLight;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Renders a scene with a high polygon model.
 * 
 * @author BudgiePanic
 */
public class LargeModelDemo extends BaseDemo {

    protected static final String modelName = "dragon.obj";

    int threshold = 10;

    @Override
    protected String getName() { return "dragon.ppm"; }

    @Override
    protected Camera getCamera() { 
        return new TimingWrapper(
            new SuperSamplingCamera( /* resolutions to try: 640*480 | 800*600 */
                new PinHoleCamera(456, 320, AngleHelp.toRadians(70), View.makeViewMatrix(makePoint(0, 5, -7), makePoint(0, 2, 1), Directions.up))
                , SuperSamplingCamera.defaultMode)
        );
    }

    @Override
    protected World createWorld() {
        // load the dragon model
        System.out.println("INFO: attempting to load " + modelName);
        var world = new World();
        world.addLight(new PointLight(makePoint(0, 20, -20), Colors.white));
        // world.addLight(new AreaLight(Colors.white, makePoint(0, 20, -20), Directions.up, Directions.right, 2, 2, AreaLight.randomSamples));
        try {
            File file = new File(modelName);
            var lines = FileUtils.readLines(file, Charset.defaultCharset());
            var modelData = WavefrontObjectLoader.parseObj(lines, Material.defaultMaterial());
            System.out.println("INFO: model has " + modelData.triangles().size() + " triangles");
            System.out.println("INFO: model has " + modelData.vertices().size() + " verticies");
            var model = WavefrontObjectLoader.objectToGroup(modelData, Transforms.identity().assemble());
            model.divide(threshold);
            System.out.println("INFO: model local extent is " + model.bounds().toString());
            System.out.println("INFO: model global extent is " + model.bounds().transform(model.transform()).toString());
            world.addShape(model);
            System.out.println("INFO: model was loaded successfully");
        } catch (IOException e) {
                System.out.println("WARN: could not load model " + modelName);
                System.out.print("WARN: ");
                System.out.println(e);
        }
        return world;
    }
    
}
