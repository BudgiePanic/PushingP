package com.BudgiePanic.rendering.scene;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NormalCameraTest {
    @Test
    void testScaledNormalRemapping() {
        var mapper = NormalCamera.scaled;
        var normal = makeVector(-1, 0, 0);
        var result = mapper.apply(normal);
        var expected = makeVector(0, 0.5f, 0.5f);
        assertEquals(expected, result);
    }
    @Test
    void testScaledNormalRemappingA() {
        var mapper = NormalCamera.scaled;
        var normal = makeVector(-1, 1, -1).normalize();
        var result = mapper.apply(normal);
        var expected = makeVector(0.2113f, 0.7886f, 0.2113f);
        assertEquals(expected, result);
    } 
}
