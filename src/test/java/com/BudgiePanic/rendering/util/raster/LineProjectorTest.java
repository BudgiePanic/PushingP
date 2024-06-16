package com.BudgiePanic.rendering.util.raster;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.transform.View;

public class LineProjectorTest {

    @Test
    void testProject() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(0, 0, 1), makePoint(), Directions.up));
        var result = LineProjector.project(makePoint(-1, 0, 0), makePoint(1, 0, 0), camera);
        var expected = new int[] {0,5,  10,5};
        assertEquals(expected, result);
    }

    @Test
    void testProjectBehind() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(0, 0, -1), makePoint(), Directions.up));
        var result = LineProjector.project(makePoint(-1, 0, 0), makePoint(1, 0, 0), camera);
        var expected = new int[] {0,5,  10,5};
        assertTrue(Arrays.equals(result, expected), expected.toString() + " " + result.toString());
    }

    @Test
    void testProjectA() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var result = LineProjector.project(makePoint(-1, 0, -1), makePoint(1, 0, -1), camera);
        var expected = new int[] {0,5,  10,5};
        assertEquals(expected, result);
    }

    @Test
    void testProjectB() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,1), Directions.up));
        var result = LineProjector.project(makePoint(-1, 0, 1), makePoint(1, 0, 1), camera);
        var expected = new int[] {0,5,  10,5};
        assertEquals(expected, result);
    }

}
