package com.BudgiePanic.rendering.scene;

import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Super sampling anti-aliasing camera.
 *
 * @author BudgiePanic
 */
public class SuperSamplingCamera implements Camera {

    public static interface SampleMode {
        /**
         * Generate the color in a pixel.
         * @param world
         *   The world the camera is imaging.
         * @param camera
         *   The child camera that ray casting is deferred to.
         * @param pixelColumn
         *   The x coordinate of the center of the pixel.
         * @param pixelRow
         *   The y coordinate of the center of the pixel.
         * @param time
         *   The time when the pixel was exposed.
         * @return
         *   The color of the pixel, whose center is at position [pixelColumn, pixelRow]
         */
        Color pixelAt(World world, Camera camera, float pixelColumn, float pixelRow, float time);
    }

    /**
     * Fixed anti-aliasing pattern samples every pixel the same number of times, with the same sample points.
     */
    protected static interface FixedPattern extends SampleMode {
        default Color pixelAt(World world, Camera camera, float pixelColumn, float pixelRow, float time) {
            final var sampleLocations = subPixelLocations();
            final int numbSubPixels = sampleLocations.size();
            float red = 0, green = 0, blue = 0;
            for (final var point : sampleLocations) {
                Color color = camera.pixelAt(world, pixelColumn + point.a(), pixelRow + point.b(), time);
                red += color.getRed(); 
                green += color.getGreen();
                blue += color.getBlue();
            }
        return new Color(red / numbSubPixels, green / numbSubPixels, blue / numbSubPixels);
        }
        /**
         * Get the ray origins for a pixel, offset from [0.5, 0.5]
         * @return
         */
        List<Pair<Float, Float>> subPixelLocations();
    }

    /**
     * Performs 4 samples per pixel. Samples are offset by 0.25 towards each corner.
     */
    public static final SampleMode grid = new Grid();

    /**
     * Performs 16 samples per pixel.
     */
    public static final SampleMode denseGrid = new DenseGrid();

    /**
     * Performs 13 samples per pixel. 4 inner Grid samples, plus a center sample, and 9 samples along the edge of the pixel.
     */
    public static final SampleMode highResolution = new Quincunx();

    /**
     * A four by four grid rotated by 30 degrees. Rotated grid can perform better than grid in some circumstances.
     */
    public static final SampleMode rotatedGrid = new RotatedGrid(6);

    public static final SampleMode dynamicCornerGrid = new DynamicSampler(0.05f);

    private final static class Grid implements FixedPattern {
        final static List<Pair<Float, Float>> pattern = List.of(
            new Pair<>(-0.25f, -0.25f),
            new Pair<>(0.25f, 0.25f),
            new Pair<>(-0.25f, 0.25f),
            new Pair<>(0.25f, -0.25f)
        );
        @Override
        public List<Pair<Float, Float>> subPixelLocations() { return pattern; }
    }

    private final static class Quincunx implements FixedPattern {
        // Pattern:
        // x   x   x
        //   x   x
        // x   x   x 
        //   x   x
        // x   x   x
        final static List<Pair<Float, Float>> pattern = List.of(
            new Pair<>(0f, 0f),

            new Pair<>(-0.25f, -0.25f),
            new Pair<>(0.25f, 0.25f),
            new Pair<>(-0.25f, 0.25f),
            new Pair<>(0.25f, -0.25f),

            new Pair<>(-0.5f, -0.5f),
            new Pair<>(0.5f, 0.5f),
            new Pair<>(-0.5f, 0.5f),
            new Pair<>(0.5f, -0.5f),

            new Pair<>(0f, -0.5f),
            new Pair<>(0f, 0.5f),
            new Pair<>(-0.5f, 0f),
            new Pair<>(0.5f, 0f)
        );
        @Override
        public List<Pair<Float, Float>> subPixelLocations() { return pattern; }
    }

    private final static class DenseGrid implements FixedPattern {
        final static List<Pair<Float, Float>> pattern = List.of(
            new Pair<>(0.125f - 0.5f, 0.125f - 0.5f),
            new Pair<>(0.375f - 0.5f, 0.125f - 0.5f),
            new Pair<>(0.625f - 0.5f, 0.125f - 0.5f),
            new Pair<>(0.875f - 0.5f, 0.125f - 0.5f),

            new Pair<>(0.125f - 0.5f, 0.375f - 0.5f),
            new Pair<>(0.375f - 0.5f, 0.375f - 0.5f),
            new Pair<>(0.625f - 0.5f, 0.375f - 0.5f),
            new Pair<>(0.875f - 0.5f, 0.375f - 0.5f),

            new Pair<>(0.125f - 0.5f, 0.625f - 0.5f),
            new Pair<>(0.375f - 0.5f, 0.625f - 0.5f),
            new Pair<>(0.625f - 0.5f, 0.625f - 0.5f),
            new Pair<>(0.875f - 0.5f, 0.625f - 0.5f),

            new Pair<>(0.125f - 0.5f, 0.875f - 0.5f),
            new Pair<>(0.375f - 0.5f, 0.875f - 0.5f),
            new Pair<>(0.625f - 0.5f, 0.875f - 0.5f),
            new Pair<>(0.875f - 0.5f, 0.875f - 0.5f)
        );
        @Override
        public List<Pair<Float, Float>> subPixelLocations() { return pattern; }
    }

    protected final static class RotatedGrid implements FixedPattern {
        // TODO the rotated grid could be improved to scatter points across the entire pixel region, current implementation ignores the corners.
        // TODO This could be easily achieved by duplicating the raw points 8 times and surrounding the initial points with more points by using these offsets:
        // TODO (+1,+1) (-1,-1) (+1,-1) (-1,+1) (+1,0) (0,+1) (-1,0) (0,-1)
        // TODO should aim to reach parity with the wikipedia reference image: https://en.wikipedia.org/wiki/Supersampling#/media/File:Supersampling_-_Checker.svg
        /**
         * creates points on a uniform grid within the region [0,0] to [1,1]
         * @param numbPoints
         *   The number of points to place in the unit square. Must be a power of 2. Recommended values are perfect squares: 4, 16, 64.
         * @return
         *   Unrotated points distributed across a unit square in a grid pattern.
         */
        protected static List<Pair<Float, Float>> rawPoints(int numbPoints) {
            final int pointsPerRow = (int) Math.sqrt(numbPoints);
            final float pointSpacing = (float) (1 / Math.sqrt(numbPoints));
            final List<Pair<Float, Float>> answer = new ArrayList<>(numbPoints);
            for (int x = 0; x < pointsPerRow; x++) {
                for (int y = 0; y < pointsPerRow; y++) {
                    answer.add(new Pair<>(
                        (x + 0.5f) * pointSpacing, 
                        (y + 0.5f) * pointSpacing)
                    );
                }
            }
            return answer;
        }
        /**
         * Only calculate the points once at object initialization.
         */
        protected final List<Pair<Float, Float>> cachedPoints;
        /**
         * Create a new rotated grid. translates raw points to be centered at [0,0] instead of [0.5,0.5], scales 2x and then rotates by -30 degrees.
         * Filters any points that lie outside the unit square after the transformation.
         * @param power
         *   The power of two for number of points. numb points = 2^power. recommended values > 6 and 4.
         */
        protected RotatedGrid(int power) {
            final int numbPoins = 1 << power;
            final var translateRotateScale = Transforms.identity().translate(-0.5f, -0.5f, 0).scale(2).rotateZ(toRadians(-30f)).assemble(); // -26.6
            var temp = rawPoints(numbPoins).stream().
                map(p -> new Tuple(p.a(), p.b(), 0)).
                map(translateRotateScale::multiply).
                map(t -> new Pair<>(t.x, t.y)).
                filter(p -> p.a() >= -0.5f && p.a() <= 0.5f && p.b() >= -0.5f && p.b() <= 0.5f).
                collect(Collectors.toList());
            this.cachedPoints = temp;
        }
        @Override
        public List<Pair<Float, Float>> subPixelLocations() { return cachedPoints; }
    }

    /**
     * Dynamic sampler starts by sampling the center of the pixel and the four corners.
     * If the color difference between the center of the pixel and the corner is large, that quarter of the pixel is sampled again
     */
    protected final static class DynamicSampler implements SampleMode {

        /**
         * Create a new dynamic sampler.
         * @param threshold
         *   The maximum euclidean distance between two sample point colors before a pixel quadrant is subsampled.
         */
        DynamicSampler(float threshold) { this.thresholdSquared = threshold * threshold; }

        /**
         * Accumulator indexing keys.
         */
        private static final int red = 0, green = 1, blue = 2;
        /**
         * Recursion bean courier to carry common elements between recursion calls.
         */
        protected static record PixelArgs(World world, Camera camera, float time, float[] accumulator) {
            void add(Color color) {
                this.accumulator[red] += color.getRed();
                this.accumulator[green] += color.getGreen();
                this.accumulator[blue] += color.getBlue();
            }
        }
        /**
         * How deep the recursion should go.
         */
        protected static final int recursionLimit = 4;
        /**
         * The euclidean distance threshold used to determine if a pixel's subquadrant should be sampled with more points.
         * value will likely need tinkering. 
         * If the ray tracer used a proper color space, this value would become the 'threshold' and would become a function of the middle pixel color (dynamically calculated).
         * See: https://en.wikipedia.org/wiki/Color_difference
         */
        protected final float thresholdSquared;

        // TODO replace with sealed interface?
        protected static enum Corner {
            none,
            topLeft,
            topRight,
            bottomRight,
            bottomLeft;
        }

        protected int pixelAt(final PixelArgs args, final float pixelColumn, final float pixelRow, final int depth, final Corner corner, final Color prevMiddle, final Color prevCorner) {
            if (depth == recursionLimit) { return 0; }
            // determine the color in each corner and the middle of this pixel sub quadrant
            final float offset = offset(depth);
            final float down = pixelRow - offset;
            final float up = pixelRow + offset;
            final float left = pixelColumn - offset;
            final float right = pixelColumn + offset;
            // get the 5 sample colors
            Color middle = args.camera.pixelAt(args.world, pixelColumn, pixelRow, args.time);
            args.add(middle);
            Color topRight = null; 
            Color topLeft = null;
            Color bottomLeft = null; 
            Color bottomRight = null;
            switch (corner) {
              case none:
                topLeft = args.camera.pixelAt(args.world, left, up, args.time);
                args.add(topLeft);
                topRight = args.camera.pixelAt(args.world, right, up, args.time);
                args.add(topRight);
                bottomLeft = args.camera.pixelAt(args.world, left, down, args.time);
                args.add(bottomLeft);
                bottomRight = args.camera.pixelAt(args.world, right, down, args.time);
                args.add(bottomRight);
                break;
              case topLeft:
                topRight = args.camera.pixelAt(args.world, right, up, args.time);
                args.add(topRight);
                topLeft = prevCorner;
                bottomLeft = args.camera.pixelAt(args.world, left, down, args.time);
                args.add(bottomLeft);
                bottomRight = prevMiddle;
                break;
              case topRight:
                topLeft = args.camera.pixelAt(args.world, left, up, args.time);
                args.add(topLeft);
                topRight = prevCorner;
                bottomLeft = prevMiddle;
                bottomRight = args.camera.pixelAt(args.world, right, down, args.time);
                args.add(bottomRight);
                break;
              case bottomLeft:
                topLeft = args.camera.pixelAt(args.world, left, up, args.time);
                args.add(topLeft);
                topRight = prevMiddle;
                bottomLeft = prevCorner;
                bottomRight = args.camera.pixelAt(args.world, right, down, args.time);
                args.add(bottomRight);
                break;
              case bottomRight:
                topLeft = prevMiddle;
                topRight = args.camera.pixelAt(args.world, right, up, args.time);
                args.add(topRight);
                bottomLeft = args.camera.pixelAt(args.world, left, down, args.time);
                args.add(bottomLeft);
                bottomRight = prevCorner;
                break;
            }
            // now that we know what the 5 sample colors are, and we have added them to the accumulator,
            // we need to get the distance betwen the middle color and the corner colors
            float tl = squaredDistance(middle, topLeft);
            float tr = squaredDistance(middle, topRight);
            float bl = squaredDistance(middle, bottomLeft);
            float br = squaredDistance(middle, bottomRight);
            int subSamples = 0;
            final float nextDown = pixelRow - (0.5f * offset);
            final float nextUp = pixelRow + (0.5f * offset);
            final float nextLeft = pixelColumn - (0.5f * offset);
            final float nextRight = pixelColumn + (0.5f * offset);
            if (tl > thresholdSquared) {
                subSamples += pixelAt(args, nextLeft, nextUp, depth + 1, Corner.topLeft, middle, topLeft);
            }
            if (tr > thresholdSquared) {
                subSamples += pixelAt(args, nextRight, nextUp, depth + 1, Corner.topRight, middle, topRight);
            }
            if (bl > thresholdSquared) {
                subSamples += pixelAt(args, nextLeft, nextDown, depth + 1, Corner.bottomLeft, middle, bottomLeft);
            }
            if (br > thresholdSquared) {
                subSamples += pixelAt(args, nextRight, nextDown, depth + 1, Corner.bottomRight, middle, bottomRight);
            }
            return samplesAddedBy(corner) + subSamples;
        }

        /**
         * Get the squared euclidean distance between two colors.
         * @param a
         *   The first color.
         * @param b
         *   The second color.
         * @return
         *   The squared euclidean distance between a and b.
         */
        protected static float squaredDistance(Color a, Color b) {
            final float red = b.getRed() - a.getRed();
            final float green = b.getGreen() - a.getGreen();
            final float blue = b.getBlue() - a.getBlue();
            return (red * red) + (green * green) + (blue * blue);
        }

        protected static int samplesAddedBy(Corner corner) {
            return switch (corner) {
                case none -> 5; 
                default -> 3; // corners re-use 2 samples from the last calculation
            };
        }

        /**
         * Get the point offset from the center of the pixel for a given depth.
         *
         * @param depth
         * @return
         */
        protected static float offset(int depth) {
            if (depth < 1) { throw new IllegalArgumentException("invalid depth! " + depth); }
            float answer = 0.5f;
            for (int i = 1; i < depth; i++) {
                answer *= 0.5f;
            }
            return answer;
        }

        protected static final int initalDepth = 1;

        @Override
        public Color pixelAt(World world, Camera camera, float pixelColumn, float pixelRow, float time) {
            final float[] accumulator = new float[3];
            final PixelArgs args = new PixelArgs(world, camera, time, accumulator);
            final int numbSamples = pixelAt(args, pixelColumn, pixelRow, initalDepth, Corner.none, null, null);
            return new Color(
                accumulator[red] / numbSamples,
                accumulator[green] / numbSamples,
                accumulator[blue] / numbSamples
            );
        }
    }

    /**
     * Internal camera that provides the actual image taking functionality
     */
    protected final Camera camera;

    /**
     * Provides the ray origin points for the rays case per pixel
     */
    protected final SampleMode mode;

    public SuperSamplingCamera(Camera camera, SampleMode mode) {
        this.camera = camera;
        this.mode = mode;
    }

    @Override
    public int width() { return camera.width(); }

    @Override
    public int height() { return camera.height(); }

    @Override
    public Ray createRay(float pixelColumn, float pixelRow, float time) {
        return camera.createRay(pixelColumn, pixelRow, time);
    }

    @Override
    public Color pixelAt(World world, float pixelColumn, float pixelRow, float time) {
        return mode.pixelAt(world, this.camera, pixelColumn, pixelRow, time);
    }
}
