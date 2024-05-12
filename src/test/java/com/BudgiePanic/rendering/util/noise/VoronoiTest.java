package com.BudgiePanic.rendering.util.noise;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;

public class VoronoiTest {
    @Test
    void testGetPointsInCell() {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                for (int z = 0; z < 10; z++) {
                    int points = Voronoi.getPointsInCell(x,y,z);
                    assertEquals(1, points);
                }
            }
        }
    }

    @Test
    void testNoise() {
        assertDoesNotThrow(() -> {
            var noise = Voronoi.noise(10.3, 4, 5.6);
            noise = Voronoi.noise(-10.3, -4, -5.6);
            assertTrue(noise <= 1);
        });
    }

    @Test
    void testMetric() {
        var result = Voronoi.distanceMetric.distance(0, 0, 0, 1, 0, 0);
        assertEquals(1, result);
        result = Voronoi.distanceMetric.distance(10, 10, 10, 10.1, 10.1, 10.1);
        assertEquals(0, FloatHelp.compareFloat(0.1732, result));
    }

    @Test
    void testHash() {
        var a = Voronoi.hash(1, 2, 3);
        var b = Voronoi.hash(3, 2, 1);
        assertNotEquals(a, b);
        a = Voronoi.hash(0, 1, 0);
        b = Voronoi.hash(0, 0, 1);
        assertNotEquals(a, b);
    }
}
