package com.BudgiePanic.rendering.util.noise;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Worley noise implementation.
 * 
 * @author BudgiePanic
 */
public class Voronoi {
    private Voronoi() {}

    /**
     * Method to determine the distance between the sample point and the nearest feature point.
     */
    public static sealed interface Metric permits Euclidean, Manhattan, EuclideanSquared {
        /**
         * Get the distance between two points.
         * @param x1
         *   The x component of the first point.
         * @param y1
         *   The y component of the first point.
         * @param z1
         *   The z component of the first point.
         * @param x2
         *   The x component of the second point.
         * @param y2
         *   The y component of the second point.
         * @param z2
         *   The z component of the second point.
         * @return
         */
        double distance(double x1, double y1, double z1, double x2, double y2, double z2);
        /**
         * Get the maximum possible distance this metric can produce.
         * @return
         *   The maximum possible value produced by this metric.
         */
        double maxValue();
    }
    /**
     * Straight line distance. Values can range from zero to square root of three.
     */
    protected static final class Euclidean implements Metric {
        @Override
        public double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
            final double x = x2 - x1, y = y2 - y1, z = z2 - z1;
            return Math.sqrt((x * x) + (y * y) + (z * z));
        }
        @Override
        public double maxValue() { return Math.sqrt(3.0); }
    }
    /**
     * Manhattan distance metric. Values can range from zero to three.
     */
    protected static final class Manhattan implements Metric {
        @Override
        public double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
            return Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2);
        }
        @Override
        public double maxValue() { return 3; }
    }
    /**
     * Straightline distance squared metric. Output Value can range from 0 to 3.
     */
    protected static final class EuclideanSquared implements Metric {
        @Override
        public double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
            final double x = x2 - x1, y = y2 - y1, z = z2 - z1;
            return (x * x) + (y * y) + (z * z);
        }
        @Override
        public double maxValue() { return 3; }
    }

    /**
     * The manhattan distance metric between sample point and feature point.
     */
    public static final Metric manhattan = new Manhattan();

    /**
     * The straightline distance metric between sample point and feature point.
     */
    public static final Metric euclidean = new Euclidean();

    /**
     * The straightline squared distance metric between the sample point and feature point.
     */
    public static final Metric euclideanSquared = new EuclideanSquared();

    /**
     * Remap pseudo random voronoi noise value to be in the range zero to one.
     * @param value
     *   The noise value
     * @param metric
     *   The distance metric used when generating the noise value.
     * @return
     *   A remapped pseudo random value between zero and one.
     */
    public static final double normNoise(double value, Metric metric) {
        final double oldMin = 0.0, oldMax = metric.maxValue(), newMin = 0.0, newMax = 1.0;
        return ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
    }

    /**
     * Remap pseudo random voronoi noise to be clamped between zero and one.
     * @param value
     *   The noise value that was generated.
     * @return
     *   A pseudo random value between zero and one.
     */
    public static final double clampNoise(double value) {
        return value > 1.0 ? 1.0 : value;
    }

    /**
     * Offset container to find coordinates of adjacent cells.
     */
    private static final record Triple(int x, int y, int z) {}

    /**
     * Cell offsets to reach adjacent cells to the cell the sample point resides within.
     */
    protected static final List<Triple> offsets = List.of(
        new Triple(0, 0, 0), // the current cell.

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
     * Most values will be between 0 and 1 but some values may exceed 1. 
     * Gets the noise value for 1 through to 'n' neighbor values.
     *
     * @param x
     *   The x component of the location being sampled.
     * @param y
     *   The y component of the location being sampled.
     * @param z
     *   The z component of the location being sampled.
     * @param n
     *   The closest neighbor number. Should be between 1 and 4. Supports up to 27.
     * @param power
     *   The fractional power to calculate the noise with. Leave as zero for default results. Higher values makes noise features smaller and weaker.
     * @param metric
     *   The distance metric used to determine the distance between cell feature points and the sample point.
     * @return
     *   A pseudo random value between 0.0 and Metric::maxValue 
     */
    public static final double[] noise(double x, double y, double z, int n, double power, Metric metric) {
        if (n < 2 || n > 4) {
            System.out.println("WARN: voronoi noise called with invalid n: " + n);
            if (n == 1) {
                System.out.println("WARN: try call noise overload instead for n == 1");
            }
            if (n < 1) {
                System.out.println("WARN: falling back to n = 2");
                n = 2;
            }
            if (n > 27) {
                System.out.println("WARN: falling back to n = 27");
                n = 27;
            }
        }
        // apply the power
        x = Math.pow(2, power) * x;
        y = Math.pow(2, power) * y;
        z = Math.pow(2, power) * z;

        final int cellX = (int) Math.floor(x);
        final int cellY = (int) Math.floor(y);
        final int cellZ = (int) Math.floor(z);

        // A non-naive implementation would use a different data structure to track only the best n values seen so far.
        final double[] distances = new double[offsets.size()];
        int index = 0;
        // TODO possible optimization: find the straight line distance to the neighboring cell
        // TODO if that distance is larger than the best distance to point already seen, we don't need to check that cell any further.
        for (final var offset : offsets) {
            final int cellA = cellX + offset.x, cellB = cellY + offset.y, cellC = cellZ + offset.z;
            final int seed = hash(cellA, cellB, cellC);
            final var rng = new Random(seed); // We might need a more light weight random number generator
            final double pointX = cellA + rng.nextDouble();
            final double pointY = cellB + rng.nextDouble();
            final double pointZ = cellC + rng.nextDouble();
            final double distance = metric.distance(x, y, z, pointX, pointY, pointZ);
            distances[index] = distance;
            index++;
        }
        Arrays.sort(distances);
        for (int i = 0; i < distances.length - 1; i++) { // scale the noise values down
            distances[i] = Math.pow(2, -power) * distances[i];
        }
        return Arrays.copyOf(distances, n);
    }

     *
     * @param x
     *   The x component of the location being sampled.
     * @param y
     *   The y component of the location being sampled.
     * @param z
     *   The z component of the location being sampled.
     * @param metric
     *   The distance metric used to determine the distance between cell feature points and the sample point.
     * @return
     *   A pseudo random value between 0.0 and Metric::maxValue 
     */
    public static final double noise(double x, double y, double z, Metric metric) {
        final int cellX = (int) Math.floor(x);
        final int cellY = (int) Math.floor(y);
        final int cellZ = (int) Math.floor(z);

        double bestDistance = Double.MAX_VALUE;
        // TODO possible optimization: find the straight line distance to the neighboring cell
        // TODO if that distance is larger than the best distance to point already seen, we don't need to check that cell any further.
        for (final var offset : offsets) {
            final int cellA = cellX + offset.x, cellB = cellY + offset.y, cellC = cellZ + offset.z;
            final int seed = hash(cellA, cellB, cellC);
            final var rng = new Random(seed); // We might need a more light weight random number generator
            final double pointX = cellA + rng.nextDouble();
            final double pointY = cellB + rng.nextDouble();
            final double pointZ = cellC + rng.nextDouble();
            final double distance = metric.distance(x, y, z, pointX, pointY, pointZ);
            if (distance < bestDistance) {
                bestDistance = distance;
            }
        }
        return bestDistance;
    }

    /**
     * Pseudo random voronoi noise.
     * Most values will be between 0 and 1 but some values may exceed 1. 
     * The theoractical maximum value this algorithm can produce is sqrt(3) but in testing 9,000,000 test values we found 24,000 values exceeded 1.0
     * and the largest we observered was 1.235. Uses the straightline distance metric.
     *
     * @param x
     *   The x component of the location being sampled.
     * @param y
     *   The y component of the location being sampled.
     * @param z
     *   The z component of the location being sampled.
     * @return
     *   A pseudo random value between 0.0 and sqrt(3.0) (1.732050) 
     */
    public static final double noise(double x, double y, double z) {
        return noise(x, y, z, euclidean);
    }

    protected static int getPointsInCell(int cellX, int cellY, int cellZ) {
        // The algorithm as described in the paper uses a much more sophisticated method to choose how many points go into each grid cell.
        // But I don't currently understand how it works, and just having 1 point in each cell is perfectly valid.
        return 1;
    }

    /**
     * Hash an integer.
     * @param x
     *   The value to hash.
     * @return
     *   The hash of x.
     */
    protected static int hash(int x) {
        final int magic = 0x45d9f3b;
        x = ((x >>> 16) ^ x) * magic;
        x = ((x >>> 16) ^ x) * magic;
        x = (x >>> 16) ^ x;
        return x;
    }

    /**
     * Combination of hash values
     * @param x
     *   First value to hash
     * @param y
     *   Second value to hash
     * @param z
     *   Third value to hash
     * @return
     *   Combines and hashes (x,y,z)
     */
    protected static int hash(int x, int y, int z) {
        // hash(x) + hash(y) + hash(z);
        return hash(x + hash(y + hash(z)));
    }

}
