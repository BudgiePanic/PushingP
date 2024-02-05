package com.BudgiePanic.rendering.util.light;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.pattern.BiOperation;
import com.BudgiePanic.rendering.util.pattern.BiPattern;

public class PhongTest {
    
    static Material material;
    static Tuple position;
    static Tuple eye; // A vector, the direction of the eye
    static Tuple normal; // A vector, the normal of the surface being shaded
    static PointLight light;

    @BeforeEach
    void resetTestHarness() {
        material = Material.defaultMaterial();
        position = Tuple.makePoint();
        eye = null;
        normal = null;
        light = null;
    }

    @Test
    void testDirectLighting() {
        eye = Tuple.makeVector(0, 0, -1);
        normal = Tuple.makeVector(0, 0, -1);
        light = new PointLight(Tuple.makePoint(0, 0, -10), Colors.white);
        var result = Phong.compute(material, light, position, eye, normal);
        var expected = new Color(1.9f, 1.9f, 1.9f);
        assertEquals(expected, result);
    }

    @Test
    void testLightingEyeOffNormal() {
        var sqrtTwoOverTwo = (float) (Math.sqrt(2.0) / 2.0);
        eye = Tuple.makeVector(0, sqrtTwoOverTwo, -sqrtTwoOverTwo);
        normal = Tuple.makeVector(0,0,-1);
        light = new PointLight(Tuple.makePoint(0, 0, -10), Colors.white);
        var result = Phong.compute(material, light, position, eye, normal);
        var expected = new Color(1f, 1f, 1f);
        assertEquals(expected, result);
    }

    @Test
    void testLightingEyeSquareWithNormal() {
        eye = Tuple.makeVector(0, 0, -1);
        normal = Tuple.makeVector(0, 0, -1);
        light = new PointLight(Tuple.makePoint(0, 10, -10), Colors.white);
        var result = Phong.compute(material, light, position, eye, normal);
        var expected = new Color(0.7364f, 0.7364f, 0.7364f);
        assertEquals(expected, result);
    }

    @Test
    void testLightEyeInReflection() {
        var sqrtTwoOverTwo = (float) (Math.sqrt(2.0) / 2.0);
        eye = Tuple.makeVector(0, -sqrtTwoOverTwo, -sqrtTwoOverTwo);
        normal = Tuple.makeVector(0,0,-1);
        light = new PointLight(Tuple.makePoint(0, 10, -10), Colors.white);
        var result = Phong.compute(material, light, position, eye, normal);
        var expected = new Color(1.63638f, 1.63638f, 1.63638f);
        assertEquals(expected, result);
    }

    @Test
    void testLightEyeBehindSurface() {
        eye = Tuple.makeVector(0,0,-1);
        normal = Tuple.makeVector(0,0,-1);
        light = new PointLight(Tuple.makePoint(0, 0, 10), Colors.white);
        var result = Phong.compute(material, light, position, eye, normal);
        var expected = new Color(0.1f, 0.1f, 0.1f);
        assertEquals(expected, result);
    }

    @Test
    void testDefaultMaterialProperties() {
        var material = Material.defaultMaterial();
        var expected = new Material(Colors.white, 0.1f, 0.9f, 0.9f, 200f);
        assertEquals(expected, material);
    }

    @Test
    void testShadowLighting() {
        eye = Tuple.makeVector(0, 0, -1);
        normal = Tuple.makeVector(0, 0, -1);
        light = new PointLight(Tuple.makePoint(0, 0, -10), Colors.white);
        var result = Phong.compute(material, light, position, eye, normal, true, Optional.empty());
        var expected = new Color(0.1f, 0.1f, 0.1f);
        assertEquals(expected, result);
    }

    @Test
    void testPatternLighting() {
        var material = new Material(new BiPattern(BiOperation.stripe, Colors.white, Colors.black), 1, 0, 0, Material.defaultShininess);
        var eye = Tuple.makeVector(0, 0, -1);
        var normal = Tuple.makeVector(0, 0, -1);
        var light = new PointLight(Tuple.makePoint(0, 0, -10), Colors.white);
        var resultA = Phong.compute(material, light, Tuple.makePoint(0.9f,0,0), eye, normal);
        var resultB = Phong.compute(material, light, Tuple.makePoint(1.1f,0,0), eye, normal);
        assertEquals(Colors.white, resultA);
        assertEquals(Colors.black, resultB);
    }
}

