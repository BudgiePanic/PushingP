package com.BudgiePanic.rendering.util.noise;

import java.util.List;
import java.util.Random;

/**
 * Worley noise implementation.
 * 
 * @author BudgiePanic
 */
public class Voronoi {
    private Voronoi() {}

    public static sealed interface Metric permits Euclidean, Manhattan, EuclideanSquared {
        double distance(double x1, double y1, double z1, double x2, double y2, double z2);
    }
    protected static final class Euclidean implements Metric {
        @Override
        public double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
            final double x = x2 - x1, y = y2 - y1, z = z2 - z1;
            return Math.sqrt((x * x) + (y * y) + (z * z));
        }
    }
    protected static final class Manhattan implements Metric {
        @Override
        public double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
            return Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2);
        }
    }
    protected static final class EuclideanSquared implements Metric {
        @Override
        public double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
            final double x = x2 - x1, y = y2 - y1, z = z2 - z1;
            return (x * x) + (y * y) + (z * z);
        }
    }

    protected static final Metric distanceMetric = new Euclidean();

    private static final record Triple(int x, int y, int z) {}

    protected static final List<Triple> offsets = List.of(
        new Triple(0, 0, 0),

        new Triple(-1, 1, -1),
        new Triple(-1, 1, 0),
        new Triple(-1, 1, 1),
        new Triple(0, 1, -1),
        new Triple(0, 1, 0),
        new Triple(0, 1, 1),
        new Triple(1, 1, -1),
        new Triple(1, 1, 0),
        new Triple(1, 1, 1),

        new Triple(-1, -1, -1),
        new Triple(-1, -1, 0),
        new Triple(-1, -1, 1),
        new Triple(0, -1, -1),
        new Triple(0, -1, 0),
        new Triple(0, -1, 1),
        new Triple(1, -1, -1),
        new Triple(1, -1, 0),
        new Triple(1, -1, 1),

        new Triple(-1, 0, -1),
        new Triple(-1, 0, 0),
        new Triple(-1, 0, 1),
        new Triple(0, 0, -1),
        new Triple(0, 0, 1),
        new Triple(1, 0, -1),
        new Triple(1, 0, 0),
        new Triple(1, 0, 1)
    );

    /**
     * Pseudo random voronoi noise.
     *
     * @param x
     *   The x component of the location being sampled.
     * @param y
     *   The y component of the location being sampled.
     * @param z
     *   The z component of the location being sampled.
     * @return
     *   A pseudo random value between 0.0 and 1.0.
     */
    public static final double noise(double x, double y, double z) {
        final int cellX = (int) Math.floor(x);
        final int cellY = (int) Math.floor(y);
        final int cellZ = (int) Math.floor(z);

        double bestDistance = Double.MAX_VALUE;
        // TODO possible optimization: find the straight line distance to the neighboring cell
        // TODO if that distance is larger than the best distance to point already seen, we don't need to check that cell any further.
        for (final var offset : offsets) {
            final int seed = hash(cellX + offset.x, cellY + offset.y, cellZ + offset.z);
            final var rng = new Random(seed); // We might need a more light weight random number generator
            final double pointX = cellX + rng.nextDouble();
            final double pointY = cellY + rng.nextDouble();
            final double pointZ = cellZ + rng.nextDouble();
            final double distance = distanceMetric.distance(x, y, z, pointX, pointY, pointZ);
            if (distance < bestDistance) {
                bestDistance = distance;
            }
        }
        return bestDistance;
    }

    protected static int getPointsInCell(int cellX, int cellY, int cellZ) {
        // The algorithm as described in the paper uses a much more sophisticated method to choose how many points go into each grid cell.
        // But I don't currently understand how it works, and just having 1 point in each cell is perfectly valid.
        return 1;
    }

    /**
     * Hash an integer.
     * @param x
     * @return
     */
    protected static int hash(int x) {
        final int magic = 0x45d9f3b;
        x = ((x >>> 16) ^ x) * magic;
        x = ((x >>> 16) ^ x) * magic;
        x = (x >>> 16) ^ x;
        return x;
    }

    /**
     * Arithmetic sum of hash values
     * @param x
     * @param y
     * @param z
     * @return
     */
    protected static int hash(int x, int y, int z) {
        return hash(x) + hash(y) + hash(z);
    }


}
