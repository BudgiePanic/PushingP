package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;
import com.BudgiePanic.rendering.util.pattern.CoordinateMapper;
import com.BudgiePanic.rendering.util.pattern.Pattern2D;
import com.BudgiePanic.rendering.util.pattern.TextureMap;
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
        return new PinHoleCamera(500, 500, AngleHelp.toRadians(60), View.makeViewMatrix(makePoint(0,0,-3), makePoint(0, 0, 0), Directions.up));
    }

    @Override
    protected World createWorld() {
        World world = new World();

        world.addLight(new PointLight(makePoint(-3, 5, -10), Colors.white));
        world.addShape(new Plane(Transforms.identity().rotateX(AngleHelp.toRadians(-70)).rotateY(AngleHelp.toRadians(10)).translate(0, -3, 2).assemble(), 
            Material.pattern(
                new TextureMap(
                    // Pattern2D.checker(2, 2, Pattern2D.solidColor(Colors.green), Pattern2D.solidColor(Colors.white)),
                    Pattern2D.mapCheck(Colors.white, Colors.red, Colors.green, Colors.blue, Colors.blue.add(Colors.red)),
                    CoordinateMapper.planar, Transforms.identity().assemble()
                )
            )
            //Material.pattern(new BiPattern(BiOperation.checker, Colors.green, Colors.white))
        ));
        world.addShape(new Cylinder(Transforms.identity().scale(1, Math.PI, 1).translate(0, -0.5, 0).rotateX(Math.PI/2).assemble(), 
            Material.pattern(
                new TextureMap(
                    Pattern2D.checker(16, 8, Pattern2D.solidColor(Colors.green), Pattern2D.solidColor(Colors.white)),
                    CoordinateMapper.cylindircal, Transforms.identity().assemble()
                )
            ), 1.0, 0.0, true
        ));
        world.addShape(new Sphere(Transforms.identity().rotateY(AngleHelp.toRadians(15)).assemble(), 
            Material.pattern(
                new TextureMap(
                    Pattern2D.checker(30, 10, Pattern2D.solidColor(Colors.green), Pattern2D.solidColor(Colors.white)),
                    CoordinateMapper.sphere, Transforms.identity().assemble() // translation can affect the resulting checker pattern
                )
            )
        ));

        return world;
    }
    
}
