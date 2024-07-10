/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
        entry("-dragon_demo", ()-> new LargeModelDemo()),
        entry("-big_scene_demo", ()-> new BigSceneDemo()),
        entry("-normal_bump_demo", ()-> new NormalBumpDemo()),
        entry("-texture_demo", ()-> new TextureMapDemo()),
        entry("-line_demo", ()-> new LineDrawerDemo()),
        entry("-line_drawer_demo", ()-> new Scratch())
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
