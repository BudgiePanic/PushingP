package com.BudgiePanic.rendering.scene;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.View;

public class BasePerspectiveCameraTest {

    protected static final class TestCamera extends BasePerspectiveCamera {

        public TestCamera(int width, int height, double fov, double focalDistance, Matrix4 transform) { super(width, height, fov, focalDistance, transform); }
        @Override
        public Ray createRay(double pixelColumn, double pixelRow, double time) { return null; }
        @Override
        public Color pixelAt(World world, double pixelColumn, double pixelRow, double time) { return null;}
    }

    @Test
    void testProject() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(0, 0, 1), makePoint(), Directions.up));
        var resultA = camera.project(makePoint(-1, 0, 0));
        var resultB =  camera.project(makePoint(1, 0, 0));
        var expectedA = new int[] {0,5};
        var expectedB = new int[] {10 ,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectBehind() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(0, 0, -1), makePoint(), Directions.up));
        var resultA = camera.project(makePoint(-1, 0, 0));
        var resultB =  camera.project(makePoint(1, 0, 0));
        // A quirk of our camera transform is that when the transform 'makes' the camera looks in the +ve z direction
        // it effectively reflects transformed points about the 'x' and 'z' axis, the knock on effect is our screen coords will be flipped
        // ultimately this doesn't matter because a line from A to B will look the exact same as a line from B to A
        var expectedA = new int[] {10 ,5};
        var expectedB = new int[] {0,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectA() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var resultA = camera.project(makePoint(-1, 0, -1));
        var resultB =  camera.project(makePoint(1, 0, -1));
        var expectedA = new int[] {0,5};
        var expectedB = new int[] {10 ,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectB() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,1), Directions.up));
        var resultA = camera.project(makePoint(-1, 0, 1));
        var resultB =  camera.project(makePoint(1, 0, 1));
        var expectedA = new int[] {10 ,5};
        var expectedB = new int[] {0,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectC() {
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var resultA = camera.project(makePoint(-1, 0, -1.1));
        var resultB =  camera.project(makePoint(1, 0, -1.1));
        var expectedA = new int[] {0,5};
        var expectedB = new int[] {10 ,5};
        assertTrue(Arrays.equals(resultA, expectedA), "expected:" + Arrays.toString(expectedA) + " actual:" + Arrays.toString(resultA));
        assertTrue(Arrays.equals(resultB, expectedB), "expected:" + Arrays.toString(expectedB) + " actual:" + Arrays.toString(resultB));
    }

    @Test
    void testProjectD() {
        // Make sure camera clipping is working (only part of the line segment can be seen by the camera)
        fail("test not implemented yet");
    }

    @Test
    void testProjectE() {
        // verify the behvaiour when we try to draw a line the camera can't see
        fail("test not implemented yet");
    }

    @Test
    void testClippingPlaneDistance() {
        // create various clipping planes and points, verify the point distance to plane makes sense
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var plane = camera.new ClippingPlane(makePoint(), Directions.up);

        var distance = plane.distanceTo(makePoint(0, 1, 0));
        assertTrue(FloatHelp.compareFloat(1, distance) == 0);

        distance = plane.distanceTo(makePoint(0, -1, 0));
        assertTrue(FloatHelp.compareFloat(-1, distance) == 0);

        distance = plane.distanceTo(makePoint(0, -3.5, 0));
        assertTrue(FloatHelp.compareFloat(-3.5, distance) == 0);

        distance = plane.distanceTo(makePoint(0, 3.5, 0));
        assertTrue(FloatHelp.compareFloat(3.5, distance) == 0);
    }

    @Test
    void testClippingPlaneDistanceA() {
        // create various clipping planes and points, verify the point distance to plane makes sense
        var camera = new PinHoleCamera(10, 10, AngleHelp.toRadians(90), View.makeViewMatrix(makePoint(), makePoint(0,0,-1), Directions.up));
        var plane = camera.new ClippingPlane(makePoint(), Directions.left);

        var distance = plane.distanceTo(makePoint(0, 1, 0));
        assertTrue(FloatHelp.compareFloat(0, distance) == 0);

        distance = plane.distanceTo(makePoint(1, 0, 0));
        assertTrue(FloatHelp.compareFloat(-1, distance) == 0);

        distance = plane.distanceTo(makePoint(-1, 0, 0));
        assertTrue(FloatHelp.compareFloat(1, distance) == 0);
    }

}
