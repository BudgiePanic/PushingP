package com.BudgiePanic.rendering.util.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Unit tests for the rotation matrix builder utility.
 * 
 * @author BudgiePanic
 */
public class RotationTest {
    
    @Test
    void testRotationXaxis() {
        var point = Tuple.makePoint(0, 1, 0);
        var rotate45Deg = Rotation.buildXRotationMatrix((float)(Math.PI / 4.0));
        var rotate90Deg = Rotation.buildXRotationMatrix((float)(Math.PI / 2.0));
        var resultA = rotate45Deg.multiply(point);
        var expectedA = Tuple.makePoint(0, (float)((Math.sqrt(2)/2.0)), (float)((Math.sqrt(2)/2.0)));
        assertEquals(expectedA, resultA);
        var resultB = rotate90Deg.multiply(point);
        var expectedB = Tuple.makePoint(0, 0, 1f);
        assertEquals(expectedB, resultB);
    }

    @Test
    void testRotationXInverse() {
        var point = Tuple.makePoint(0, 1, 0);
        var rotateNeg45Deg = Rotation.buildXRotationMatrix((float)(Math.PI / 4.0)).inverse();
        var result = rotateNeg45Deg.multiply(point);
        var expected = Tuple.makePoint(0, (float)((Math.sqrt(2)/2.0)), -(float)((Math.sqrt(2)/2.0)));
        assertEquals(expected, result);
    }

    @Test
    void testYRotation() {
        var point = Tuple.makePoint(0, 0, 1);
        var rotate45Deg = Rotation.buildYRotationMatrix((float)(Math.PI / 4.0));
        var rotate90Deg = Rotation.buildYRotationMatrix((float)(Math.PI / 2.0));
        var resultA = rotate45Deg.multiply(point);
        var expectedA = Tuple.makePoint((float)((Math.sqrt(2)/2.0)), 0, (float)((Math.sqrt(2)/2.0)));
        assertEquals(expectedA, resultA);
        var resultB = rotate90Deg.multiply(point);
        var expectedB = Tuple.makePoint(1, 0, 0);
        assertEquals(expectedB, resultB);
    }

    @Test
    void testZRotation() {
        var point = Tuple.makePoint(0, 1, 0);
        var rotate45Deg = Rotation.buildZRotationMatrix((float)(Math.PI / 4.0));
        var rotate90Deg = Rotation.buildZRotationMatrix((float)(Math.PI / 2.0));
        var resultA = rotate45Deg.multiply(point);
        var expectedA = Tuple.makePoint(-(float)((Math.sqrt(2)/2.0)), (float)((Math.sqrt(2)/2.0)), 0);
        assertEquals(expectedA, resultA);
        var resultB = rotate90Deg.multiply(point);
        var expectedB = Tuple.makePoint(-1, 0, 0);
        assertEquals(expectedB, resultB);
    }

}
