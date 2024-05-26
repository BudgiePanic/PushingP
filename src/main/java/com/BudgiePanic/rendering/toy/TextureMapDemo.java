package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.CoordinateMapper;
import com.BudgiePanic.rendering.util.pattern.CubeTextureMap;
import com.BudgiePanic.rendering.util.pattern.Pattern2D;
import com.BudgiePanic.rendering.util.pattern.TextureMap;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.Plane;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

public class TextureMapDemo extends BaseDemo {

    @Override
    protected String getName() { return "texture_demo.ppm"; }

    @Override
    protected Camera getCamera() {
        return new PinHoleCamera(800, 400, 0.8, View.makeViewMatrix(makePoint(0,0, -20), makePoint(0, 0, 0), Directions.up));
    }

    @Override
    protected World createWorld() {
        World world = new World();

        world.addLight(new PointLight(makePoint(0, 100, -100), Colors.white.multiply(0.25)));
        world.addLight(new PointLight(makePoint(0, -100, -100), Colors.white.multiply(0.25)));
        world.addLight(new PointLight(makePoint(-100, 0, -100), Colors.white.multiply(0.25)));
        world.addLight(new PointLight(makePoint(100, 0, -100), Colors.white.multiply(0.25)));
        
        
        final Color red = Colors.red, yellow = new Color(1, 1, 0), brown = new Color(1,0.5,0), 
        green = Colors.green, cyan = new Color(0,1,1), blue = Colors.blue, purple = new Color(1, 0, 1),
        white = Colors.white;
        final var left = Pattern2D.mapCheck(yellow, cyan, red, blue, brown);
        final var front = Pattern2D.mapCheck(cyan, red, yellow, brown, green);
        final var right = Pattern2D.mapCheck(red, yellow, purple, green, white);
        final var back = Pattern2D.mapCheck(green, purple, cyan, white, blue);
        final var up = Pattern2D.mapCheck(brown, cyan, purple, red, yellow);
        final var down = Pattern2D.mapCheck(purple, brown, green, blue, white);
        final var cube = new CubeTextureMap(front, left, right, up, down, back);

        final Material material = new Material(
            cube,
            0.2, 0.8, 0, Material.defaultShininess, Material.defaultReflectivity, 
            Material.defaultTransparency, Material.defaultRefractiveIndex, 
            Material.defaultShadowCast, Material.defaultNormalBump
        );

        Cube[] cubes = new Cube[] {
            new Cube(Transforms.identity().rotateY(0.7854).rotateX(0.7854).translate(-6, 2, 0).assemble(), material),
            new Cube(Transforms.identity().rotateY(2.3562).rotateX(0.7854).translate(-2, 2, 0).assemble(), material),
            new Cube(Transforms.identity().rotateY(3.927).rotateX(0.7854).translate(2, 2, 0).assemble(), material),
            new Cube(Transforms.identity().rotateY(5.4978).rotateX(0.7854).translate(6, 2, 0).assemble(), material),
            new Cube(Transforms.identity().rotateY(0.7854).rotateX(-0.7854).translate(-6, -2, 0).assemble(), material),
            new Cube(Transforms.identity().rotateY(2.3562).rotateX(-0.7854).translate(-2, -2, 0).assemble(), material),
            new Cube(Transforms.identity().rotateY(3.927).rotateX(-0.7854).translate(2, -2, 0).assemble(), material),
            new Cube(Transforms.identity().rotateY(5.4978).rotateX(-0.7854).translate(6, -2, 0).assemble(), material)
        };
        for (Cube box : cubes) {
            world.addShape(box);
        }
        return world;
    }
    
}
