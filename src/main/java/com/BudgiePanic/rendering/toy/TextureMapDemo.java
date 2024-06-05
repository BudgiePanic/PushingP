package com.BudgiePanic.rendering.toy;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.FocusCamera;
import com.BudgiePanic.rendering.scene.SuperSamplingCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.pattern.CoordinateMapper;
import com.BudgiePanic.rendering.util.pattern.CubeTextureMap;
import com.BudgiePanic.rendering.util.pattern.Pattern2D;
import com.BudgiePanic.rendering.util.pattern.TextureMap;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.shape.composite.CompoundOperation;
import com.BudgiePanic.rendering.util.shape.composite.CompoundShape;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;

/**
 * Demonstration of cube mapping and texture mapping capabilities of PushingP.
 *
 * earth image obtained from: https://planetpixelemporium.com/earth.html
 * cube maps obtained from this website http://www.humus.name/
 * images converted from jpg to ppm using GIMP export image functionality
 * 
 * @author BudgiePanic
 */
public class TextureMapDemo extends BaseDemo {

    @Override
    protected String getName() { return "texture_demo.ppm"; }

    @Override
    protected Camera getCamera() {
        var camera = new FocusCamera(800, 400, AngleHelp.toRadians(120), 0.09, 4.6,  View.makeViewMatrix(makePoint(0,0, 0), makePoint(0, 0, 5), Directions.up));
        return new SuperSamplingCamera(camera, SuperSamplingCamera.defaultMode);
    }

    @Override
    protected World createWorld() {
        World world = new World();

        world.addLight(new PointLight(makePoint(0, 100, 0), Colors.white));

        world.addShape(new Sphere(
            Transforms.identity().scale(0.75).translate(0, 0, 5).assemble(), 
            Material.defaultMaterial().setAmbient(0).setReflectivity(0.6).setShininess(20).setSpecular(0.6).setDiffuse(0.4))
        );

        world.addShape(new Cube(
            Transforms.identity().scale(1000).assemble(), 
            Material.pattern(new CubeTextureMap(
                Pattern2D.texture2D("posz.ppm"), // front
                Pattern2D.texture2D("negx.ppm"), // left
                Pattern2D.texture2D("posx.ppm"), // right
                Pattern2D.texture2D("posy.ppm"), // up
                Pattern2D.texture2D("negy.ppm"), // down
                Pattern2D.texture2D("negz.ppm"))). // back
                setAmbient(1).setSpecular(0).setDiffuse(0)
        ));
       
        return world;
    }
    
}
