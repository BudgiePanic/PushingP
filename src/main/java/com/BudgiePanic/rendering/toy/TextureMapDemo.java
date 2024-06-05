package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.SuperSamplingCamera;
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

/**
 * 
 * earth image obtained from: https://planetpixelemporium.com/earth.html
 * earth image converted from jpg to ppm using GIMP export image functionality
 * 
 * @author BudgiePanic
 */
public class TextureMapDemo extends BaseDemo {

    @Override
    protected String getName() { return "texture_demo.ppm"; }

    @Override
    protected Camera getCamera() {
        return new SuperSamplingCamera(new PinHoleCamera(800, 400, 0.8, View.makeViewMatrix(makePoint(1,2, -10), makePoint(0, 1.1, 0), Directions.up)),SuperSamplingCamera.defaultMode);
    }

    @Override
    protected World createWorld() {
        World world = new World();

        world.addLight(new PointLight(makePoint(-100, 100, -100), Colors.white));

        world.addShape(new Plane(
            Transforms.identity().assemble(), 
            Material.color(Colors.white).setDiffuse(0.1).setSpecular(0).setAmbient(0).setReflectivity(0.4))
        );

        world.addShape(new Cylinder(
            Transforms.identity().assemble(), 
            Material.color(Colors.white).setDiffuse(0.2).setSpecular(0).setAmbient(0).setReflectivity(0.1),
            0.1, 0, true)
        );

        world.addShape(new Sphere(
            Transforms.identity().rotateY(1.9).translate(0, 1.1, 0).assemble(),
            Material.pattern(new TextureMap(Pattern2D.texture2D("earthmap1k.ppm"), CoordinateMapper.sphere)).setSpecular(0.1).setShininess(10))
        );
       
        return world;
    }
    
}
