package com.BudgiePanic.rendering.scene;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.RandomSuppliers;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Focus Camera has adjustable focal length and aperture.
 * Requires more rays to be cast to generate a pixel color than pinhole.
 * 
 * @author BudgiePanic
 */
public class FocusCamera extends BasePerspectiveCamera {

    /**
     * Default randomness source for focus camera. Non-deterministic, varies output each run, thread safe.
     */
    public static final Supplier<Float> defaultRandomness = RandomSuppliers.threadSafeRandomSupplier; 

    /**
     * Constant value 'randomness' for testing
     */
    public static final Supplier<Float> constantOutput = RandomSuppliers.noRandom;

    /**
     * The suggested value of rays to cast per pixel.
     */
    public static final int defaultRaysPerPixel = 3;
    
    /**
     * The size of the hole that light enters the camera through.
     * Smaller values cause a deeper depth of field. Larger values cause a narrow depth of field.
     * A deep depth of field means everything in the image appears in focus.
     * A narrow depth of field means only the subject at the focal distance is in focus, other elements are blurred.
     */
    protected final float aperture;
    /**
     * The distance of the camera's imaging plane to the aperture.
     */
    protected final float focalDistance;
    /**
     * The number of rays to cast per pixel through the aperture.
     * The color from each ray is averaged together to form the final pixel color.
     */
    protected final int raysPerPixel;
    /**
     * Supplier of ray direction randomness.
     */
    protected final Supplier<Float> randomnessSource;

    /**
     * Canonical constructor. Use a convience constructor instead.
     *
     * @param width
     *   The number of columns on the camera's imaging plane.
     * @param height
     *   The number of rows on the camera's imaging plane.
     * @param aperture
     *   The size of the hole that light enters the camera through. Smaller values cause a deeper depth of field. Larger values cause a narrow depth of field.
     * @param focalDistance
     *   The distance of the camera's imaging plane to the aperture. Should be a positive number.
     * @param transform
     *   Transform to move into camera orientation space.
     * @param raysPerPixel
     *   The number of rays to cast through the apeture for each pixel.
     * @param randomnessSource
     *   Source of randomness of ray directions.
     */
    public FocusCamera(int width, int height, float aperture, float focalDistance, Matrix4 transform, int raysPerPixel, Supplier<Float> randomnessSource) {
        super(width, height, focalDistance, transform);
        this.focalDistance = focalDistance;
        this.aperture = aperture;
        this.raysPerPixel = raysPerPixel;
        this.randomnessSource = randomnessSource;
    }

    /**
     * Create a new focus camera.
     * @param width
     *   The number of columns on the camera's imaging plane.
     * @param height
     *   The number of rows on the camera's imaging plane.
     * @param aperture
     *   The size of the hole that light enters the camera through. Smaller values cause a larger depth of field. Larger values cause a narrow depth of field.
     * @param focalDistance
     *   The distance of the camera's imaging plane to the aperture.
     * @param transform
     *   Transform to move into camera orientation space.
     */
    public FocusCamera(int width, int height, float aperture, float focalDistance, Matrix4 transform) {
        this(width, height, aperture, focalDistance, transform, defaultRaysPerPixel, defaultRandomness);
    }

    @Override
    public Ray createRay(int pixelColumn, int pixelRow) {
        // pre condition checks
        if (pixelColumn < 0 || pixelColumn > this.width) throw new IllegalArgumentException("invalid pixel column for camera");
        if (pixelRow < 0 || pixelRow > this.height) throw new IllegalArgumentException("invalid pixel row for camera");
        // compute the offset from the edge of the canvas to the center of the pixel
        final var xOffset = (pixelColumn + 0.5f) * this.pixelSize;
        final var yOffset = (pixelRow + 0.5f) * this.pixelSize;

        final var worldX = this.halfWidth - xOffset;
        final var worldY = this.halfHeight - yOffset;
        final float worldZ = -focalDistance;
        
        final var cameraInverse = this.transform.inverse();
        final var pixel = cameraInverse.multiply(makePoint(worldX, worldY, worldZ));

        final var aperturePoint = randomPointOnAperture();
        final var origin = cameraInverse.multiply(aperturePoint);

        final var direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction);
    }

    /**
     * Generate a random point on the camera's apeture.
     * The camera's aperture is always originated at [0,0,0]. Randomness if offset in the XY dimensions only (z will always be zero).
     * @return
     *   A random point on the camera's apeture.
     */
    private Tuple randomPointOnAperture() {
        final var radius = aperture * Math.sqrt(randomnessSource.get());
        final var angle = randomnessSource.get() * 2 * Math.PI;
        final var xOffset = radius * Math.cos(angle);
        final var yOffset = radius * Math.sin(angle);
        return makePoint((float) xOffset, (float) yOffset, 0f);
    }

    @Override
    public Canvas takePicture(World world, Canvas canvas) {
        // see: https://sighack.com/post/averaging-rgb-colors-the-right-way
        // pre condition check: is the canvas big enough for the camera?
        if (canvas == null || canvas.getHeight() < this.height || canvas.getWidth() < this.width) throw new IllegalArgumentException();
        List<Pair<Integer, Integer>> jobs = new ArrayList<>(this.height * this.width);
        // generate jobs to execute in parrallel
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                jobs.add(new Pair<Integer,Integer>(col, row));
            }
        }
        jobs.parallelStream().forEach(pixel -> {
            final int column = pixel.a();
            final int row = pixel.b();
            float red = 0;
            float green = 0;
            float blue = 0;
            // cast n rays into the scene for this pixel 
            // and then average the colors from the rays together
            for (int i = 0; i < this.raysPerPixel; i++) {
                final var ray = createRay(column, row);
                final var color = world.computeColor(ray);
                red += color.getRed(); // red += color.getRed() * color.getRed();
                green += color.getGreen(); // Green += color.getGreen() * color.getGreen();
                blue += color.getBlue(); // Blue += color.getBlue() * color.getBlue();
            }
            red /= raysPerPixel; // red = (float) Math.sqrt(red / raysPerPixel);
            green /= raysPerPixel; // green = (float) Math.sqrt(green / raysPerPixel);
            blue /= raysPerPixel; // blue = (float) Math.sqrt(blue / raysPerPixel);
            canvas.writePixel(column, row, new Color(red, green, blue));
        });
        return canvas;
    }
}
