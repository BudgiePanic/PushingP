package com.BudgiePanic.rendering.scene;

import com.BudgiePanic.rendering.scene.DepthCamera.DepthMode;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.shape.LinearMotionShape;

/**
 * Velocity camera fills the canvas with the velocity of the shapes it images.
 * 
 * @author BudgiePanic
 */
public class VelocityCamera implements Camera {

    /**
     * No processing of the velocity values.
     */
    public static final DepthMode raw = DepthCamera.rawUnclampedDepthValues;

    /**
     * Scales velocity values to be between 0 and max velocity.
     */
    public static final DepthMode scaled = DepthCamera.clampedDepthValues;

    /**
     * The recommended default mode for processing raw velocity values.
     */
    public static final DepthMode defaultMode = scaled;

    /**
     * How to process the raw velocity values.
     */
    protected final DepthMode mode;

    /**
     * The time the image should be taken.
     */
    protected final double time;

    /**
     * The internal camera to copy orientation from.
     */
    protected final BasePerspectiveCamera internal;

    /**
     * Create a new velocity camera.
     *
     * @param camera
     *   The internal camera to copy orientation from.
     * @param mode
     *   How to process the raw velovity values.
     * @param time
     *   The time the image should be taken at.
     */
    public VelocityCamera(BasePerspectiveCamera camera, DepthMode mode, double time) {
        this.internal = camera;
        this.mode = mode;
        this.time = time;
    }

    /**
     * Create a velocity camera that uses default processing mode and images at time = zero.
     *
     * @param camera
     *   The internal camera to copy orientation from.
     */
    public VelocityCamera(BasePerspectiveCamera camera) {
        this(camera, VelocityCamera.defaultMode, 0.0);
    }

    @Override
    public int width() { return internal.width; }

    @Override
    public int height() { return internal.height; }

    @Override
    public Ray createRay(double pixelColumn, double pixelRow, double time) {
        // create pinhole camera rays so the velocity map image is crisp and in-focus
        // TODO: code smell here, this method is 99% similar to PinHoleCamera::createRay, there can probably be an abstraction
        if (pixelColumn < 0 || pixelColumn > internal.width) throw new IllegalArgumentException("invalid pixel column for camera");
        if (pixelRow < 0 || pixelRow > internal.height) throw new IllegalArgumentException("invalid pixel row for camera");
        final var xOffset = (pixelColumn) * internal.pixelSize;
        final var yOffset = (pixelRow) * internal.pixelSize;
        final var worldX = internal.halfWidth - xOffset;
        final var worldY = internal.halfHeight - yOffset;
        final double worldZ = -internal.focalDistance;
        final var cameraInverse = internal.transform.inverse();
        final var pixel = cameraInverse.multiply(Tuple.makePoint(worldX, worldY, worldZ));
        final var origin = cameraInverse.multiply(Tuple.makePoint());
        final var direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction, time);
    }

    @Override
    public Color pixelExposureAt(World world, double pixelColumn, double pixelRow) {
        return pixelAt(world, pixelColumn, pixelRow, this.time);
    }

    @Override
    public Color pixelAt(World world, double pixelColumn, double pixelRow, double time) {
        final var zero = Tuple.makePoint();
        final var ray = createRay(pixelColumn, pixelRow, time);
        final var intersections = world.intersect(ray);
        final Tuple velocity = intersections.flatMap(Intersection::Hit).map(i -> {
            // go up the parent chain, checking if any parents are motion shapes
            var parent = i.shape().parent();
            while (parent.isPresent()) {
                if (parent.get() instanceof LinearMotionShape) {
                    return ((LinearMotionShape)parent.get()).velocity(time);
                }
                parent = parent.get().parent();
            }
            return zero;
        }).orElseGet(() -> Colors.black);
        return new Color(velocity.x, velocity.y, velocity.z);
    }

    @Override
    public Canvas takePicture(final World world, final Canvas canvas) {
        final var velocities = Camera.super.takePicture(world, canvas);
        final var velocityInfo = DepthCamera.getMinMaxValues(velocities);
        velocities.writeAll(mode.apply(velocityInfo));
        return velocities;
    }
    
}
