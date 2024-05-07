package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Creates a 10*10*10 grid of spheres and measures the time taken to image it with and without the BVH optimization.
 * 
 * @author BudgiePanic
 */
public class BoundingVolumeHierarchyTest extends BaseDemo {

    private boolean flag = false;

    @Override
    protected String getName() { return "bvh_performance_test.ppm"; }

    @Override
    protected Camera getCamera() {
        return new TimingWrapper(new PinHoleCamera(1000, 1000, AngleHelp.toRadians(70), View.makeViewMatrix(makePoint(10, 10, -15), makePoint(10, 10, 5), Directions.up)));
    }

    @Override
    protected World createWorld() {
        System.out.println("INFO: running bounding volume hierarchy performance test");
        var world = new World();
        world.addLight(new PointLight(makePoint(4, 3, -6), Colors.white));
        var group = new Group(Transforms.identity().assemble());
        for (int i = 0; i < 10; i++) {
            for (int x = 0; x < 10; x++) {
                for (int z = 0; z < 10; z++) {
                    group.addShape(new Sphere(Transforms.identity().translate(x * 2, i * 2, z * 2).assemble()));
                }
            }
        }
        world.addShape(group);
        if (flag) {
            System.out.println("INFO: adding bvh optimization to world with threshold of one");
            group.divide(1);
        } else {
            System.out.println("INFO: no bvh optimization was added for this run");
        }
        flag = true;
        return world;
    }

    @Override
    public void run() {
        imageWorld();
        var b = imageWorld();
        saveImageToFile(b, fileName);
    }
    
}
