# PushingP
PushingP is an offline [Whitted ray tracing](https://en.wikipedia.org/wiki/Ray_tracing_(graphics)#Recursive_ray_tracing_algorithm) renderer written in Java.

# Project Name

PushingP is short for PushingP(ixels), a refence to the application's job of filling the pixels of the screen with color.

# Background

PushingP was developed by following _Jamis Buck's_ book **The Ray Tracer Challenge**

# Usage

### Building PusingP

To build PushingP from source, the project requires Java 21 and Maven 3 to be installed on the host device.

To build a jar file that contains PushingP and demonstration scenes run:
- `mvn clean compile assembly:single` 

The generated jar file will be in the `/target` folder.

### Using PushingP 

In the directory that contains the jar file:
- To see the available demonstration scenes: `java -jar PushingP-version.jar -demo_help`
- To run a demo scene: `java -jar PushingP-version.jar -demo_name`
  - For example: `java -jar PushingP-version.jar -soft_shadows`

PushingP can also be run using: `java -cp PushingP-version.jar com.BudgiePanic.rendering.App -argument`

You can render an image of your own scene using: 

- compile: `javac -cp ".:PushingP-version.jar" MyFile.java`
- run: `java -cp ".:PushingP-verion.jar" MyFile`

The following class generates an image of a sphere infront of a flat surface.
<br>_MyFile.java_

```java
import com.BudgiePanic.rendering.util.*;
import com.BudgiePanic.rendering.scene.*;
import com.BudgiePanic.rendering.util.light.*;
import com.BudgiePanic.rendering.util.pattern.*;
import com.BudgiePanic.rendering.util.shape.*;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.View;
import com.BudgiePanic.rendering.toy.BaseDemo;

public class MyFile extends BaseDemo {
    public static void main(String[] args) {
        new MyFile().run();
    }

    @Override
    public String getName() {
        return "my_image.ppm";
    }

    @Override
    public Camera getCamera() {
        return new SuperSamplingCamera(
            new PinHoleCamera(640, 480, AngleHelp.toRadians(90), 
            View.makeViewMatrix(Tuple.makePoint(0,1,-5), Tuple.makePoint(0,0,1), Directions.up)), 
            SuperSamplingCamera.defaultMode
        );
    }

    @Override
    public World createWorld() {
        World world = new World();

        Light light = new AreaLight(Colors.white, Tuple.makePoint(5,5,-7), Directions.forward, Directions.up, 3, 3, AreaLight.randomSamples);
        world.addLight(light);

        var sphere = new Sphere(Transforms.identity().assemble());
        world.addShape(sphere);

        var background = new Cube(Transforms.identity().scale(15,15,0.1).translate(0,0,5).assemble());
        world.addShape(background);

        return world;
    }

}
```

### Viewing images

PushingP currently exports images in the `ppm` format.
<br>`ppm` images can be viewed using an image viewing program, such as [GNU's GIMP application](https://www.gimp.org/).

# Features

PushingP has the following features:
- Ray-Shape intersection testing for:
  - Sphere
  - Cube
  - Plane
  - Triangle
  - Smooth Triangle (interpolated normals)
  - Torus
  - Cylinder
  - Cone
- Composite Shapes
  - Shape groups
  - Constructive solid geometry shapes
  - Motion shapes (adds motion blur)
  - Bounded Volume Hierarchy acceleration
- Lights
  - Point light
  - Point spot light
  - Area light
  - Area spot light
- Cameras
  - Pinhole camera
  - Focal camera
  - Shutter camera
- Postprocessing
  - Supersample anti-aliasing
- Noise
  - Value noise
  - Voronoi noise
  - Perlin noise
- Materials
  - Phong lighting model
  - Texture mapping supported for:
    - Spheres
    - Cubes
    - Cylinders
  - Patterns
    - stripes
    - rings 
    - gradient
    - checker
    - radial gradient
    - solid color
- IO
  - `*.ppm` image writer
  - `*.ppm` texture importer
  - `*.obj` object importer    
- debugging
  - 3D line segment rasterizer
  - Scene normals camera
  - Scene depth camera
  - Scene velocity camera
  - Render time measurement
  - Single threaded camera

# License

PushingP is released under the Apache 2.0 open source license 
