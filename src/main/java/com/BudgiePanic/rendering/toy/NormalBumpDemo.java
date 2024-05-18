package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.reporting.TimingWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.SuperSamplingCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.noise.Perlin;
import com.BudgiePanic.rendering.util.noise.Voronoi;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Demonstration scene showing how functions can be used to manipulate shape's normal vectors for a more interesting image.
 *
 * @author BudgiePanic
 */
public class NormalBumpDemo extends BaseDemo {

    @Override
    protected String getName() { return "normal_bump.ppm"; }

    @Override
    protected Camera getCamera() {
        return new TimingWrapper(
            new SuperSamplingCamera(
              new PinHoleCamera(1024, 1024, AngleHelp.toRadians(60), View.makeViewMatrix(makePoint(0, 0.5, -5), makePoint(0, 0.7, 0), Directions.up)),
              SuperSamplingCamera.defaultMode // SuperSamplingCamera.none // Turn anti aliasing off for a faster render
        ));
    }

    @Override
    protected World createWorld() {
        System.out.println("INFO: running normal bump function demo");
        World world = new World();
        world.addShape(new Cube(Transforms.identity().scale(10).assemble(), Material.color(Colors.white.multiply(0.85))));
        world.addShape(new Plane(Transforms.identity().assemble(), Material.color(Colors.white).setReflectivity(0.2).
            setNormalBump((normal, point)->{
                var voronoi = Voronoi.noise(point.x, point.y, point.z, 3, 4, Voronoi.manhattan);
                double noise = voronoi[0];
                noise = Voronoi.normNoise(noise, Voronoi.manhattan);
                return normal.add(0, noise, noise).normalize();
            })
        ));
        world.addShape(new Sphere(
            Transforms.identity().scale(0.5).translate(1, 0.5, 0).assemble(),
            Material.color(Colors.blue).setNormalBump((normal, point)->{
                final double power = 2.5;
                final double scale = 0.5;
                // scale the points down
                double x = Math.pow(2, power) * point.x;
                double y = Math.pow(2, power) * point.y;
                // find the integer cell that the point is in
                final int cellX = (int) Math.floor(x); 
                final int cellY = (int) Math.floor(y); 
                // find the relative position of the point in the cell
                x = x - cellX;
                y = y - cellY;
                // scale the noise amplitude
                x *= scale;
                y *= scale;
                // add the relative position as a noise value to the normal vector
                return normal.add(x,y,0).normalize();
            })
        ));
        world.addShape(new Sphere(
            Transforms.identity().translate(-1.5, 1, 0).assemble(),
            Material.color(Colors.green).setNormalBump((normal, point)->{
                double noise = 0.0;
                for (int p = 3; p < 6; p++) {
                    noise += Perlin.noise(point.x, point.y, point.z, p);
                }
                return normal.add(noise, noise, noise).normalize();
            })
        ));
        world.addShape(new Sphere(
            Transforms.identity().scale(2).translate(0, 2, 3).assemble(),
            Material.color(Colors.red).setNormalBump((normal, point)->{
                double value = Math.sin(point.y * Math.pow(2, 3));
                value *= 0.5;
                return normal.add(0, value, 0).normalize();
            })
        ));
        world.addLight(new PointLight(makePoint(0, 3.5, -9), Colors.white));

        return world;
    }
    
}
