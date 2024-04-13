package com.BudgiePanic.rendering.util.shape;

import java.util.List;

import org.junit.jupiter.api.Test;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

public class CubeTest {
    
    @Test
    void testLocalIntersect() {
        var cube = new Cube(Matrix4.identity());
        var origins = List.of(
            makePoint(5,0.5f,0),
            makePoint(-5,0.5f,0),
            makePoint(0.5f,5,0),
            makePoint(0.5f,-5,0),
            makePoint(0.5f,0,5),
            makePoint(0.5f,0,-5),
            makePoint(0,0.5f,0)
        );
        var directions = List.of(
            makeVector(-1,0,0),
            makeVector(1,0,0),
            makeVector(0,-1,0),
            makeVector(0,1,0),
            makeVector(0,0,-1),
            makeVector(0,0,1),
            makeVector(0,0,1)
        );
        var expectedIntersect1 = List.of(
            4, // +x
            4, // -x
            4, // +y
            4, // -y
            4, // +z
            4, // -z
            -1    // inside
        );
        var expectedIntersect2 = List.of(
            6,
            6,
            6,
            6,
            6,
            6,
            1
        );
        for(int i = 0; i < directions.size(); i++) {
            var ray = new Ray(origins.get(i), directions.get(i));
            var result = cube.localIntersect(ray);
            assertTrue(result.isPresent(), Integer.toString(i));
            var intersections = result.get();
            assertEquals(2, intersections.size(), Integer.toString(i));
            var expected1 = expectedIntersect1.get(i);
            var expected2 = expectedIntersect2.get(i);
            var actual1 = intersections.get(0).a();
            var actual2 = intersections.get(1).a();
            assertTrue(FloatHelp.compareFloat(expected1, actual1) == 0, Integer.toString(i) + expected1 + actual1);
            assertTrue(FloatHelp.compareFloat(expected2, actual2) == 0, Integer.toString(i) + expected2 + actual2);
        }
    }

    @Test
    void testNoIntersection() {
        var cube = new Cube(Matrix4.identity());
        var origins = List.of(
            makePoint(-2f,0f,0f),
            makePoint(0f,-2f,0f),
            makePoint(0f,0f,-2f),
            makePoint(2f,0f,2f),
            makePoint(0f,2f,2f),
            makePoint(2f,2f,0f)
        );
        var directions = List.of(
            makeVector(0.2673f,5345f,0.8018f),
            makeVector(0.8018f,0.2673f,0.5345f),
            makeVector(0.5345f,0.8018f,0.2673f),
            makeVector(0f,0f,-1f),
            makeVector(0f,-1f,0f),
            makeVector(-1f,0f,0)
        );
        for(int i = 0; i < directions.size(); i++) {
            var ray = new Ray(origins.get(i), directions.get(i));
            var result = cube.localIntersect(ray);
            assertTrue(result.isEmpty(), Integer.toString(i));
        }
    }

    @Test
    void testLocalNormal() {
        var points = List.of(
            makePoint(1f, 0.5f, -0.8f),
            makePoint(-1f, -0.2f, 0.9f),
            makePoint(-0.4f, 1f, -0.1f),
            makePoint(0.3f, -1f, -0.1f),
            makePoint(-0.6f, 0.3f, 1f),
            makePoint(0.4f, 0.4f, -1f),
            makePoint(1f, 1f, 1f),
            makePoint(-1f, -1f, -1f)
        );
        var expectedNormals = List.of(
            makeVector(1f, 0f, 0f),
            makeVector(-1f, 0f, 0f),
            makeVector(0f, 1f, 0f),
            makeVector(0f, -1f, 0f),
            makeVector(0f, 0f, 1f),
            makeVector(0f, 0f, -1f),
            makeVector(1f, 0f, 0f),
            makeVector(-1f, 0f, 0f)
        );
        var cube = new Cube(Matrix4.identity());
        for (int i = 0; i < points.size(); i++) {
            var output = cube.localNormal(points.get(i));
            var expected = expectedNormals.get(i);
            assertEquals(expected, output);
        }
    }

    @Test
    void testCubeSolid() {
        var cube = new Cube(Matrix4.identity());
        assertTrue(cube.isSolid());
    }

    @Test
    void testCubeHit() {
        var cube = new Cube(Matrix4.identity());
        var ray = new Ray(makePoint(1.1, 0, 0), makeVector(1, 0, 0));
        var result = cube.intersect(ray).get();
        assertTrue(Intersection.Hit(result).isEmpty());
        
        result = cube.intersect(new Ray(makePoint(-1.1, 0, 0), makeVector(-1, 0, 0))).get();
        assertTrue(Intersection.Hit(result).isEmpty());

        result = cube.intersect(new Ray(makePoint(0, 1.1, 0), makeVector(0, 1, 0))).get();
        assertTrue(Intersection.Hit(result).isEmpty());

        result = cube.intersect(new Ray(makePoint(0, -1.1, 0), makeVector(0, -1, 0))).get();
        assertTrue(Intersection.Hit(result).isEmpty());

        result = cube.intersect(new Ray(makePoint(0, 0, 1.1), makeVector(0, 0, 1))).get();
        assertTrue(Intersection.Hit(result).isEmpty());

        result = cube.intersect(new Ray(makePoint(0, 0, -1.1), makeVector(0, 0, -1))).get();
        assertTrue(Intersection.Hit(result).isEmpty());
    }
}
