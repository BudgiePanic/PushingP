package com.BudgiePanic.rendering.util.light;

import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
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
        var expected = Transforms.identity().rotateX(toRadians(180f)).translate(1, 0, 0).assemble();
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

}
