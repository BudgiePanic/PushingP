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
    public static final int defaultRaysPerPixel = 6;

    /**
     * The suggested FOV for Focus camera.
     * Using a consistent fov eliminates one variable that affects the FOV of the final image.
     * (The other variable is the focal distance).
     */
    public static final float defaultFOV = 70f;
    
    /**
     * The size of the hole that light enters the camera through.
     * Smaller values cause a deeper depth of field. Larger values cause a narrow depth of field.
     * A deep depth of field means everything in the image appears in focus.
     * A narrow depth of field means only the subject at the focal distance is in focus, other elements are blurred.
     */
    protected final float aperture;
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
     * @param fov
     *   The field of view of the imaging plane. Affects the FOV of the final image.
     * @param aperture
     *   The size of the hole that light enters the camera through. Smaller values cause a deeper depth of field. Larger values cause a narrow depth of field. The aperture value may need to be smaller than you think to pull focus.
     * @param focalDistance
     *   The distance of the camera's imaging plane to the aperture. Should be a positive number. Affects the FOV of the final image. Experiment with values from 0.01f to 10f.
     * @param transform
     *   Transform to move into camera orientation space.
     * @param raysPerPixel
     *   The number of rays to cast through the apeture for each pixel.
     * @param randomnessSource
     *   Source of randomness of ray directions.
     */
    public FocusCamera(int width, int height, float fov, float aperture, float focalDistance, Matrix4 transform, int raysPerPixel, Supplier<Float> randomnessSource) {
        super(width, height, fov, focalDistance, transform);
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
     * @param fov
     *   The field of view of the imaging plane. Affects the FOV of the final image.
     * @param aperture
     *   The size of the hole that light enters the camera through. Try starting at 0.1f.
     *   Smaller values cause a larger depth of field. Larger values cause a narrow depth of field.
     * @param focalDistance
     *   The distance of the camera's imaging plane to the aperture. Affects the FOV of the final image. Larger focal distances narrows the FOV.
     * @param transform
     *   Transform to move into camera orientation space.
     */
    public FocusCamera(int width, int height, float fov, float aperture, float focalDistance, Matrix4 transform) {
        this(width, height, fov, aperture, focalDistance, transform, defaultRaysPerPixel, defaultRandomness);
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
        this(width, height, defaultFOV, aperture, focalDistance, transform, defaultRaysPerPixel, defaultRandomness);
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
        if (canvas == null || canvas.getHeight() < this.height || canvas.getWidth() < this.width) throw new IllegalArgumentException();
        List<Pair<Integer, Integer>> jobs = generateJobs();
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
                // see: https://sighack.com/post/averaging-rgb-colors-the-right-way
                red += color.getRed() * color.getRed(); // red += color.getRed();
                green += color.getGreen() * color.getGreen(); // green += color.getGreen();
                blue += color.getBlue() * color.getBlue(); // blue += color.getBlue();
            }
            // average the squared colors
            red = (float) Math.sqrt(red / raysPerPixel); // red /= raysPerPixel;
            green = (float) Math.sqrt(green / raysPerPixel); // green /= raysPerPixel; 
            blue = (float) Math.sqrt(blue / raysPerPixel); // blue /= raysPerPixel; 
            canvas.writePixel(column, row, new Color(red, green, blue));
        });
        return canvas;
    }
}
