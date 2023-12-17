package com.BudgiePanic.rendering.util.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Unit tests for concatination of tranform matrices together.
 * TODO create fluent API via transform factory class.
 * 
 * @author BudgiePanic
 */
public class TransformTest {
    
    @Test
    void testRotateScaleTranslate() {
        var point = Tuple.makePoint(1, 0, 1);
        var rotate = Rotation.buildXRotationMatrix((float)(Math.PI / 2.0));
        var scale = Scale.makeScaleMatrix(5, 5, 5);
        var translate = Translation.makeTranslationMatrix(10, 5, 7);

        var point2 = rotate.multiply(point);
        var expected2 = Tuple.makePoint(1, -1, 0);
        assertEquals(expected2, point2);

        var point3 = scale.multiply(point2);
        var expected3 = Tuple.makePoint(5, -5, 0);
        assertEquals(expected3, point3);

        var point4 = translate.multiply(point3);
        var expected4 = Tuple.makePoint(15, 0, 7);
        assertEquals(expected4, point4);
    }

    @Test
    void testChainedMatrix() {
        var point = Tuple.makePoint(1, 0, 1);
        var rotate = Rotation.buildXRotationMatrix((float)(Math.PI / 2.0));
        var scale = Scale.makeScaleMatrix(5, 5, 5);
        var translate = Translation.makeTranslationMatrix(10, 5, 7);
        // Note the chaining is 'back to front' because matrix multiplication is 
        var transform = translate.multiply(scale).multiply(rotate);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(15, 0, 7);
        assertEquals(expected, result);
    }

}
