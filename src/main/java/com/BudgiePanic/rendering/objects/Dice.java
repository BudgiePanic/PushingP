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
package com.BudgiePanic.rendering.objects;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import static com.BudgiePanic.rendering.util.matrix.Matrix4.identity;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.union;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.difference;
import com.BudgiePanic.rendering.util.shape.composite.CompoundShape;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * A compound shape that looks like a die.
 * 
 * @author BudgiePanic
 */
public class Dice extends CompoundShape {
    
    /**
     * Creates a smooth dice wtih default materials
     * @param transform
     *   The dice transform, defines the dice's world position.
     */
    public Dice(Matrix4 transform) {    
        this(transform, Material.defaultMaterial(), Material.defaultMaterial());
    }

    /**
     * Create a smooth dice.
     * @param transform
     *   The dice transform, defines the dice's world position.
     * @param cubeMaterial
     *   The material of the dice cube.
     * @param dimpleMaterial
     *   The material of the dice number dimples
     */
    public Dice(Matrix4 transform, Material cubeMaterial, Material dimpleMaterial) {
        super(
            difference,
            makeSmoothCube(Transforms.identity().translate(0.30f, 0.30f, 0.30f).scale(0.65f).assemble(), cubeMaterial),
            makeDimples(dimpleMaterial),
            transform
        );
    }

    private static Shape makeHollowSmoothCube(Matrix4 transform, Material material) {
        final var rotation = AngleHelp.toRadians(90f);
        final var max = 1;
        final var min = 0;
        // cylinders
        final var a = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateZ(-rotation).translate(0, 0, 0).assemble(), material, max, min, true) 
        {
            @Override public String toString() { return "a"; };
        }; 
        final var b = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(1, 0, 0).rotateX(rotation).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "b"; };
        }; 
        final var c = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(0, 0, 1).rotateZ(-rotation).assemble(), material, max, min, true) 
        {
            @Override public String toString() { return "c"; };
        };
        final var d = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateX(rotation).translate(0, 0, 0).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "d"; };
        };

        final var e = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(0, 0, 0).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "e"; };
        }; 
        final var f = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(1, 0, 0).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "f"; };
        }; 
        final var g = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(1, 0, 1).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "g"; };
        }; 
        final var h = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).translate(0, 0, 1).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "h"; };
        }; 

        final var i = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateZ(-rotation).translate(0, 1, 0).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "i"; };
        }; 
        final var j = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateX(rotation).translate(0, 1, 0).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "j"; };
        }; 
        final var k = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateZ(-rotation).translate(0, 1, 1).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "k"; };
        }; 
        final var l = new Cylinder(Transforms.identity().scale(0.25f, 1.0f, 0.25f).rotateX(rotation).translate(1, 1, 0).assemble(), material, max, min, true)
        {
            @Override public String toString() { return "l"; };
        }; 

        // cubes
        final var _1 = new Sphere(Transforms.identity().scale(0.25f).translate(0, 0, 0).assemble(), material)
        {
            @Override public String toString() { return "1"; };
        }; 
        final var _2 = new Sphere(Transforms.identity().scale(0.25f).translate(1, 0, 0).assemble(), material)
        {
            @Override public String toString() { return "2"; };
        }; 
        final var _3 = new Sphere(Transforms.identity().scale(0.25f).translate(1, 0, 1).assemble(), material)
        {
            @Override public String toString() { return "3"; };
        };
        final var _4 = new Sphere(Transforms.identity().scale(0.25f).translate(0, 0, 1).assemble(), material)
        {
            @Override public String toString() { return "4"; };
        }; 

        final var _5 = new Sphere(Transforms.identity().scale(0.25f).translate(0, 1, 0).assemble(), material)
        {
            @Override public String toString() { return "5"; };
        };
        final var _6 = new Sphere(Transforms.identity().scale(0.25f).translate(1, 1, 0).assemble(), material)
        {
            @Override public String toString() { return "6"; };
        };
        final var _7 = new Sphere(Transforms.identity().scale(0.25f).translate(1, 1, 1).assemble(), material)
        {
            @Override public String toString() { return "7"; };
        };
        final var _8 = new Sphere(Transforms.identity().scale(0.25f).translate(0, 1, 1).assemble(), material)
        {
            @Override public String toString() { return "8"; };
        };

        final var _1_a = new CompoundShape(union, _1, a, identity());
        final var _4_c = new CompoundShape(union, _4, c, identity());
        final var _4_cd = new CompoundShape(union, _4_c, d, identity()); 
        final var _14_adc = new CompoundShape(union, _1_a, _4_cd, identity());

        final var _8_h = new CompoundShape(union, _8, h, identity());
        final var _8_jh = new CompoundShape(union, _8_h, j, identity());
        final var _5_e = new CompoundShape(union, _5, e, identity());
        final var _58_ejh = new CompoundShape(union, _5_e, _8_jh, identity());

        final var _1584_acdejh = new CompoundShape(union, _58_ejh, _14_adc, identity());

        final var _6_i = new CompoundShape(union, _6, i, identity());
        final var _6_il = new CompoundShape(union, _6_i, l, identity());
        final var _7_k = new CompoundShape(union, _7, k, identity());
        final var _67_ikl = new CompoundShape(union, _7_k, _6_il, identity());

        final var _3_g = new CompoundShape(union, _3, g, identity());
        final var _2_f = new CompoundShape(union, _2, f, identity());
        final var _2_fb = new CompoundShape(union, _2_f, b, identity());
        final var _23_fgb = new CompoundShape(union, _3_g, _2_fb, identity());

        final var _2367_fgbikl = new CompoundShape(union, _23_fgb, _67_ikl, identity());    
        
        final var _12345678_abcdefhijkl = new CompoundShape(union, _1584_acdejh, _2367_fgbikl, transform);
        return _12345678_abcdefhijkl;
    }

    private static Shape makeSmoothCube(Matrix4 transform, Material material) {
        final var vertical = new Cube(Transforms.identity().scale(0.50f,0.75f,0.5f).translate(0.5f, 0.5f, 0.5f).assemble(), material);
        final var horizontal = new Cube(Transforms.identity().scale(0.75f,0.5f,0.5f).translate(0.5f, 0.5f, 0.5f).assemble(), material);
        final var depth = new Cube(Transforms.identity().scale(0.5f,0.5f,0.75f).translate(0.5f, 0.5f, 0.5f).assemble(), material);
        final var plus = new CompoundShape(union, vertical, horizontal, identity());
        final var _3dPlus = new CompoundShape(union, plus, depth, identity());
        final var cubeHollow = makeHollowSmoothCube(identity(), material);
        return new CompoundShape(union, cubeHollow, _3dPlus, transform);
    }

    private static Shape makeDimples(Material dimpleMaterial) {
        final var dimpleSize = 0.1f;
        var dimples = new Group(identity());
        
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.50f,1.0f,0.50f).assemble(), dimpleMaterial)
        {
            @Override public String toString() { return "1"; };
        }); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0,0.25f,0.25f).assemble(), dimpleMaterial){
            @Override public String toString() { return "2-low"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0,0.75f,0.75f).assemble(), dimpleMaterial){
            @Override public String toString() { return "2-high"; };
        }); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0.25f,0).assemble(), dimpleMaterial){
            @Override public String toString() { return "3-low"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.50f,0.50f,0).assemble(), dimpleMaterial)
        {
            @Override public String toString() { return "3-mid"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0.75f,0).assemble(), dimpleMaterial)
        {
            @Override public String toString() { return "3-high"; };
        }); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0.25f,1).assemble(), dimpleMaterial){
            @Override public String toString() { return "4-bl"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0.75f,1).assemble(), dimpleMaterial){
            @Override public String toString() { return "4-tl"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0.75f,1).assemble(), dimpleMaterial){
            @Override public String toString() { return "4-tr"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0.25f,1).assemble(), dimpleMaterial){
            @Override public String toString() { return "4-br"; };
        }); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.50f,0.5f).assemble(), dimpleMaterial){
            @Override public String toString() { return "5-1"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.25f,0.25f).assemble(), dimpleMaterial){
            @Override public String toString() { return "5-2"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.75f,0.75f).assemble(), dimpleMaterial){
            @Override public String toString() { return "5-3"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.25f,0.75f).assemble(), dimpleMaterial){
            @Override public String toString() { return "5-4"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(1,0.75f,0.25f).assemble(), dimpleMaterial){
            @Override public String toString() { return "5-5"; };
        }); 

        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0,0.25f).assemble(), dimpleMaterial){
            @Override public String toString() { return "6-1"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0,0.75f).assemble(), dimpleMaterial){
            @Override public String toString() { return "6-2"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0,0.75f).assemble(), dimpleMaterial){
            @Override public String toString() { return "6-3"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0,0.25f).assemble(), dimpleMaterial){
            @Override public String toString() { return "6-4"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.25f,0,0.50f).assemble(), dimpleMaterial){
            @Override public String toString() { return "6-5"; };
        }); 
        dimples.addShape(new Sphere(Transforms.identity().scale(dimpleSize).translate(0.75f,0,0.50f).assemble(), dimpleMaterial){
            @Override public String toString() { return "6-6"; };
        }); 
        return dimples;
    }
}
