package com.BudgiePanic.rendering;

import java.util.Map;
import java.util.function.Supplier;

import static java.util.Map.entry;

import com.BudgiePanic.rendering.toy.*;

/**
 * Hello world!
 *
 */
public class App 
{
    final static Map<String, Supplier<Runnable>> demos = Map.ofEntries(
        entry("-arty", () -> new Artillery()),
        entry("-clock", () -> new Clock()),
        entry("-shadow", () -> new Shadow()),
        entry("-sphere", () -> new DrawSphere()),
        entry("-camera", () -> new CameraDemo()),
        entry("-shadowDemo", () -> new ShadowDemo()),
        entry("-plane", () -> new PlaneDemo()),
        entry("-pattern", () -> new PatternToy()),
        entry("-refraction", () -> new RefractionDemo()),
        entry("-cube", ()-> new CubeDemo()),
        entry("-cylinder", ()-> new CylinderDemo()),
        entry("-aabb_demo", ()-> new AABBDemo()),
        entry("-acne_test", ()-> new TestScene()),
        entry("-group", ()-> new GroupDemo()),
        entry("-mesh_loader", ()-> new TriangleDemo()),
        entry("-compound_shapes", ()-> new CompoundShapeDemo()),
        entry("-soft_shadows", ()-> new AreaLightDemo()),
        entry("-donut", ()-> new TorusDemo()),
        entry("-focus", ()-> new FocusCameraDemo()),
        entry("-motion_demo", ()-> new MotionDemo()),
        entry("-spotlight", ()-> new SpotLightDemo()),
        entry("-anti_alias_demo", ()-> new AntiAliasingDemo()),
        entry("-bvh_test", ()-> new BoundingVolumeHierarchyTest()),
        entry("-dragon_demo", ()-> new LargeModelDemo())
    );

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        if (args.length > 0) {
            if (args[0].equals("-demo_help")) {
                printDemos();
                return;
            }
            var demo = demos.get(args[0]);
            if (demo == null) {
                System.out.println("Unknown demo argument: " + args[0]);
                System.out.println("use \'-demo_help\' to see available demos.");
            } else {
                demo.get().run();
            }
        } else {
            System.out.println("no arguments recieved");
            System.out.println("use \'-demo_help\' to see available demos.");
        }
    }

    static void printDemos() {
        System.out.println("supported demos:");
        demos.keySet().forEach(System.out::println);
    }
}
