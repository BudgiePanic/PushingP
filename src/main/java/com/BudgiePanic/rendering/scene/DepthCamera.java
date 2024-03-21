package com.BudgiePanic.rendering.scene;

import java.util.List;
import java.util.function.Function;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * The depth camera records the distance each ray cast out of the camera monitoring travels to intersect with an object in the scene.
 *
 * @author BudgiePanic
 */
public class DepthCamera implements Camera {
    
    /**
     * Function to process the depth values before writing them to the depth buffer.
     * TODO make this interface sealed, only permit normalized, raw, clamped
     */
    public static interface DepthMode extends Function<DepthCamera, Function<Color, Color>> {}
    
    /**
     * Writes normalized depth values (between 1 and 0) to the depth buffer.
     */
    public static final DepthMode normalizedDepthValues = (camera) -> (pixel) -> {
        final float distance = pixel.x;
        final boolean isInfinity = Float.compare(distance, Float.POSITIVE_INFINITY) == 0;
        final float normalizedDistance = isInfinity ? 0f : (distance - camera.minDistance) / (camera.maxDistance - camera.minDistance);
        return new Color(normalizedDistance, normalizedDistance, normalizedDistance);
    };
    
    /**
     * Writes the raw depth values to the depth buffer.
     */
    public static final DepthMode rawDepthValues = (camera) -> (pixel) -> {
        float distance = pixel.x;
        final boolean isInfinity = Float.compare(distance, Float.POSITIVE_INFINITY) == 0;
        distance = isInfinity ? 0f : distance;
        return new Color(distance, distance, distance);
    };
    
    /**
     * Writes clamped depth values to the depth buffer. (depth values will be between 0 and max depth).
     */
    public static final DepthMode clampedDepthValues = (camera) -> (pixel) -> {
        final float distance = pixel.x;
        final boolean isInfinity = Float.compare(distance, Float.POSITIVE_INFINITY) == 0;
        final float clampedDistance = isInfinity ? 0f : (distance) / (camera.maxDistance);
        return new Color(clampedDistance, clampedDistance, clampedDistance);
    };
    
    /**
     * The camera that is being monitored. 
     */
    protected final BasePerspectiveCamera cameraMonitoring;
    /**
     * How the depth values should be processed before being written to the depth buffer.
     */
    protected final DepthMode mode;
    /**
     * The furtherest distance to a ray intersection seen so far.
     */
    protected Float maxDistance;
    /**
     * The smallest distance to a ray-shape intersection seen so far.
     */
    protected Float minDistance;

    public DepthCamera(BasePerspectiveCamera camera, DepthMode mode) {
        this.cameraMonitoring = camera;
        this.mode = mode;
        this.maxDistance = Float.MIN_VALUE;
        this.minDistance = Float.MAX_VALUE;
    }

    @Override
    public int width() { return cameraMonitoring.width; }

    @Override
    public int height() { return cameraMonitoring.height; }

    @Override
    public Ray createRay(final int pixelColumn, final int pixelRow) {
        // create pinhole camera rays so the depth image is crisp and in-focus
        // TODO: code smell here, this method is 99% similar to PinHoleCamera::createRay, there can probably be an abstraction
        if (pixelColumn < 0 || pixelColumn > cameraMonitoring.width) throw new IllegalArgumentException("invalid pixel column for camera");
        if (pixelRow < 0 || pixelRow > cameraMonitoring.height) throw new IllegalArgumentException("invalid pixel row for camera");
        final var xOffset = (pixelColumn + 0.5f) * cameraMonitoring.pixelSize;
        final var yOffset = (pixelRow + 0.5f) * cameraMonitoring.pixelSize;
        final var worldX = cameraMonitoring.halfWidth - xOffset;
        final var worldY = cameraMonitoring.halfHeight - yOffset;
        final float worldZ = -cameraMonitoring.focalDistance;
        final var cameraInverse = cameraMonitoring.transform.inverse();
        final var pixel = cameraInverse.multiply(Tuple.makePoint(worldX, worldY, worldZ));
        final var origin = cameraInverse.multiply(Tuple.makePoint());
        final var direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction);
    }

    @Override
    public synchronized Canvas takePicture(final World world, final Canvas canvas) {
        if (canvas == null || canvas.getHeight() < cameraMonitoring.height || canvas.getWidth() < cameraMonitoring.width) throw new IllegalArgumentException();
        List<Pair<Integer, Integer>> jobs = cameraMonitoring.generateJobs();
        // intersect each pixel-ray with the world and record the distance
        jobs.parallelStream().forEach(pixel -> {
            final int column = pixel.a();
            final int row = pixel.b();
            final var ray = createRay(column, row);
            final var intersections = world.intersect(ray);
            final var distance = intersections.flatMap(Intersection::Hit).map(Intersection::a).orElse(Float.POSITIVE_INFINITY);
            final var inverseDistance = 1f / distance;
            canvas.writePixel(column, row, new Color(inverseDistance, inverseDistance, inverseDistance));
            }
        });
        canvas.writeAll(mode.apply(this));
        // reset the distances in case this function is called again with a different world supplied
        this.maxDistance = Float.MIN_VALUE; // max distance should be a local variable, but the lambdas can write to a local var...
        this.minDistance = Float.MAX_VALUE;
        return canvas;
    }
    
}
