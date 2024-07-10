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
package com.BudgiePanic.rendering.util.light;

import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class AreaSpotLightTest {
    @Test
    void testLocalPosition() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = makePoint(0, -1, 0);
        var result = light.localPosition;
        assertEquals(expected, result);
    }

    @Test
    void testLocalPositionA() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.down, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = makePoint(0, -1, 0);
        var result = light.localPosition;
        assertEquals(expected, result);
    }

    @Test
    void testLocalPositionC() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(45), toRadians(45), 2, 1);
        var expected = makePoint(0, -2 ,0);
        var result = light.localPosition;
        assertEquals(expected, result);
    }

    @Test
    void testLocalPositionD() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(60), toRadians(60), 1, 1);
        var expected = makePoint(0, -0.57735f,0); // 1/sqrt(3)
        var result = light.localPosition;
        assertEquals(expected, result);
    }

    @Test
    void testTransformGeneration() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = Matrix4.identity(); // global orientation == local orientation
        var result = light.transform;
        assertEquals(expected, result);
    }

    @Test
    void testTransformGenerationA() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.down, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = Transforms.identity().rotateX(toRadians(180f)).assemble();
        var result = light.transform;
        assertEquals(expected, result);
    }

    
    @Test
    void testTransformGenerationB() {
        var light = new AreaSpotLight(makePoint(1, 0, 0), Directions.down, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = Transforms.identity().rotateX(toRadians(180f)).translate(-1, 0, 0).assemble();
        var result = light.transform;
        assertEquals(expected, result);
    }

    @Test
    void testTransformGenerationC() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.right, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = Transforms.identity().rotateZ(toRadians(90f)).assemble();
        var result = light.transform;
        assertEquals(expected, result);
    }

    @Test
    void testTransform() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.down, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = AreaSpotLight.localDirection;
        var result = light.transform.multiply(Directions.down);
        assertEquals(expected, result);
    }

    @Test
    void testTransformA() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.right, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = AreaSpotLight.localDirection;
        var result = light.transform.multiply(Directions.right);
        assertEquals(expected, result);
    }

    @Test
    void testTransformB() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.left, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = AreaSpotLight.localDirection;
        var result = light.transform.multiply(Directions.left);
        assertEquals(expected, result);
    }

    @Test
    void testTransformC() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.forward, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = AreaSpotLight.localDirection;
        var result = light.transform.multiply(Directions.forward);
        assertEquals(expected, result);
    }

    @Test
    void testTransformD() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.backward, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = AreaSpotLight.localDirection;
        var result = light.transform.multiply(Directions.backward);
        assertEquals(expected, result);
    }

    @Test
    void testTransformE() {
        var globalDirection = makeVector(1, -1, 0).normalize();
        var light = new AreaSpotLight(makePoint(0, 0, 0), globalDirection, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = AreaSpotLight.localDirection;
        var result = light.transform.multiply(globalDirection);
        assertEquals(expected, result);
    }

    @Test
    void testTransformF() {
        var globalDirection = makeVector(1, -1, 0).normalize();
        var light = new AreaSpotLight(makePoint(0, 1, 0), globalDirection, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = AreaSpotLight.localDirection;
        var result = light.transform.multiply(globalDirection);
        assertEquals(expected, result);
    }

    @Test
    void testTransformG() {
        var globalDirection = makeVector(0, -1, 0);
        var globalPoint = makePoint(0, -1, 0);
        var light = new AreaSpotLight(makePoint(0, 0, 0), globalDirection, Colors.white, toRadians(45), toRadians(45), 1, 1);
        var expected = makePoint(0, 1, 0);
        var result = light.transform.multiply(globalPoint);
        assertEquals(expected, result);
    }

    // ILLUMINATION TESTS

    // local space
    @Test
    void testIntensityAt() {
        // cone angle = inner angle, point in the cone with no obstruction should have 1f intensity
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(45), toRadians(45), 1, 10);
        assertEquals(Matrix4.identity(), light.transform);
        World world = new World();
        var result = light.intensityAt(makePoint(0, 1, 0), world, 0);
        var expected = 1f;
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " actual " + result);
    }

    @Test
    void testIntensityAtA() {
        // inner angle = 0, point 0.5f across the cone with no obstruction should have 0.5f intensity
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(0), toRadians(30), 1, 10);
        assertEquals(Matrix4.identity(), light.transform);
        World world = new World();
        var result = light.intensityAt(makePoint(0.92818f, 1.732f, 0), world, 0);
        var expected = 0.5f;
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " actual " + result);
    }

    @Test
    void testIntensityAtAB() {
        // point behind the light should not be illuminated
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(30), toRadians(30), 1, 10);
        assertEquals(Matrix4.identity(), light.transform);
        World world = new World();
        var result = light.intensityAt(makePoint(0f, -0.1f, 0f), world, 0);
        var expected = 0f;
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " actual " + result);
    }

    @Test
    void testIntensityAtB() {
        // point outside the cone should have 0f intensity
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(30), toRadians(30), 1, 10);
        assertEquals(Matrix4.identity(), light.transform);
        World world = new World();
        var result = light.intensityAt(makePoint(2.1f, 1.732f, 0f), world, 0);
        var expected = 0f;
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " actual " + result);
    }

    // global space - light points to the right and is moved by 1 unit
    @Test
    void testIntensityAtC() {
        // cone angle = inner angle, point in the cone with no obstruction should have 1f intensity
        var light = new AreaSpotLight(makePoint(0, 1, 0), Directions.right, Colors.white, toRadians(30), toRadians(30), 1, 10);
        World world = new World();
        var result = light.intensityAt(makePoint(1, 1, 0), world, 0);
        var expected = 1f;
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " actual " + result);
    }

    @Test
    void testIntensityAtD() {
        // inner angle = 0, point 0.5f across the cone with no obstruction should have 0.5f intensity
        var light = new AreaSpotLight(makePoint(0, 1, 0), Directions.right, Colors.white, toRadians(0), toRadians(30), 1, 10);
        World world = new World();
        var result = light.intensityAt(makePoint(1.732f, 0.92818f+1f, 0), world, 0);
        var expected = 0.5f;
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " actual " + result);
    }
    

    @Test
    void testIntensityAtE() {
        // point outside the cone should have 0f intensity
        var light = new AreaSpotLight(makePoint(0, 1, 0), Directions.right, Colors.white, toRadians(0), toRadians(30), 1, 10);
        World world = new World();
        var result = light.intensityAt(makePoint(1.732f, 1+2.2f, 0), world, 0);
        var expected = 0f;
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " actual " + result);
    }

    @Test
    void testIntensityAtF() {
        // point behind the cone should have 0f intensity
        var light = new AreaSpotLight(makePoint(0, 1, 0), Directions.right, Colors.white, toRadians(0), toRadians(30), 1, 10);
        World world = new World();
        var result = light.intensityAt(makePoint(-0.1f, 0f, 0), world, 0);
        var expected = 0f;
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " actual " + result);
    }

    // other methods
    @Test
    void testInCone() {
        var light = new AreaSpotLight(makePoint(0, 0, 0), Directions.up, Colors.white, toRadians(30), toRadians(30), 1, 10);
        assertTrue(light.isInCone(makePoint(0, 1, 0)), "[0,1,0]");
        assertFalse(light.isInCone(makePoint(0, -1, 0)), "[0,-1,0]");
        assertFalse(light.isInCone(makePoint(1.5f, 0, 0)), "[1.5,0,0]");
        assertFalse(light.isInCone(makePoint(0, -0.1f, 0)), "[0,-0.1,0]");
    }
}
