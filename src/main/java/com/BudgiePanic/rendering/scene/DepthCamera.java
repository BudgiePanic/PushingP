package com.BudgiePanic.rendering.scene;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * The depth camera records the distance each ray cast out of the camera monitoring travels to intersect with an object in the scene.
 * Nearby values in the depth buffer are closer to zero. 
 *
 * @author BudgiePanic
 */
public class DepthCamera implements Camera {
    
    /**
     * Function to process a single depth value.
     * TODO make this interface sealed, only permit normalized, raw, clamped
     */
    public static interface DepthMode extends Function<Pair<Double, Double>, Function<Color, Color>> {}
    
    /**
     * Creates normalized depth values (0 for closest intersection distance, 1 for furtherest intersection distance).
     */
    public static final DepthMode normalizedDepthValues = (depthInfo) -> (pixel) -> {
        final double distance = pixel.x;
        final double minDistance = depthInfo.a();
        final double maxDistance = depthInfo.b();
        // infinity is a special case, means the ray didn't actually hit anything.
        // we need to remap infinity so as to not mess up the normalization.
        // in rasterization graphics this isn't an issue because max distance can only be as large as the far clipping plane. but since rays can miss, we can get infinities.
        final boolean isInfinity = Double.compare(distance, Double.POSITIVE_INFINITY) == 0;
        final double normalizedDistance = isInfinity ? 1.0 : (distance - minDistance) / (maxDistance - minDistance);
        return new Color(normalizedDistance, normalizedDistance, normalizedDistance);
    };
    
    /**
     * Returns the raw depth value.
     * This mode clamps rays that did not hit anything (depth = infinity) to 'max distance'.
     * NOTE: the canvas writer will clamp color value to be between 0 and 1
     *       so using this mode will likely yield an all white image as depths are usually greater than 1.
     *       This is expected behaviour.
     */
    public static final DepthMode rawDepthValues = (depthInfo) -> (pixel) -> {
        final double distance = pixel.x;
        final double maxDistance = depthInfo.b();
        final boolean isInfinity = Double.compare(distance, Double.POSITIVE_INFINITY) == 0;
        final double clampedDistance = isInfinity ? maxDistance : distance;
        return new Color(clampedDistance, clampedDistance, clampedDistance);
    };

    /**
     * Returns the raw depth value.
     * NOTE: the canvas writer will clamp color value to be between 0 and 1
     *       so this mode will likely yield and all white image as depths are usually greater than 1.
     *       This is expected behaviour.
     */
    public static final DepthMode rawUnclampedDepthValues = (depthInfo) -> (pixel) -> {
        final double distance = pixel.x;
        return new Color(distance, distance, distance);
    };
    
    /**
     * Creates clamped depth values. (depth values will be between 0 and max depth).
     */
    public static final DepthMode clampedDepthValues = (depthInfo) -> (pixel) -> {
        final double distance = pixel.x;
        final double maxDistance = depthInfo.b();
        final boolean isInfinity = Double.compare(distance, Double.POSITIVE_INFINITY) == 0;
        final double clampedDistance = isInfinity ? maxDistance : (distance) / (maxDistance);
        return new Color(clampedDistance, clampedDistance, clampedDistance);
    };

    /**
     * Determines which strategy the depth camera uses to calculate the distance values it writes to the depth buffer.
     */
    protected sealed interface DistanceMode permits DepthCamera.RayDistance, DepthCamera.PointDistance {
        /**
         * Determine the distance for a single pixel.
         * @param ray
         *   The ray that was cast into the scene by the camera to get a distance value for the pixel.
         * @param intersections
         *   The ray-shape intersections for the given pixel
         * @return
         *   The distance value to write into the depth buffer for this pixel.
         */
        double distance(Ray ray, Optional<List<Intersection>> intersections);
    }

    /**
     * Distance values are the distance you must travel along a ray to reach the
     * ray-shape intersection.
     */
    private final static class RayDistance implements DistanceMode {
        @Override
        public double distance(Ray ray, Optional<List<Intersection>> intersections) {
            return intersections.flatMap(Intersection::Hit).map(Intersection::a).orElse(Double.POSITIVE_INFINITY);
        }
    }

    /**
     * Distance values are the distance from the camera origin to the point of
     * ray-shape intersection in local camera space.
     */
    private final class PointDistance implements DistanceMode {
        /**
         * The z coordinate of the camera in local camera space.
         */
        private static final double cameraLocalOrigin = 0.0;
        @Override
        public double distance(Ray ray, Optional<List<Intersection>> intersections) {
            return intersections.flatMap(Intersection::Hit).map(i -> ray.origin().add(ray.direction().multiply(i.a()))).
            map(cameraMonitoring::transform).map(p -> cameraLocalOrigin - p.z).orElse(Double.POSITIVE_INFINITY);
        }
    }

    /**
     * Ray distance mode singleton.
     */
    protected static final DistanceMode rayDistanceInstance = new RayDistance();
    /**
     * Creates a depth mode that uses the ray-shape intersection distance.
     */
    public static final Function<DepthCamera, DistanceMode> rayDistance = (camera) -> { return rayDistanceInstance; };
    /**
     * Createss a depth modt that uses the position of the ray-shape intersection to determine the intersection distance.
     */
    public static final Function<DepthCamera, DistanceMode> pointDistance = (camera) -> { return camera.new PointDistance(); };


    /**
     * The camera that is being monitored. 
     */
    protected final BasePerspectiveCamera cameraMonitoring;
    /**
     * How the depth values should be processed before being written to the depth buffer.
     */
    protected final DepthMode mode;
    /**
     * How the depth values should be calculated from ray-shape intersections.
     */
    protected final DistanceMode distanceCalculator;

    /**
     * Convience constructor.
     * Uses the 'campedDepthValues' depth mode, which most closely matches the behaviour most users expect from a depth buffer.
     * Uses ray-shape intersection distance to determine distance value.
     * @param camera
     *   The camera to monitor.
     */
    public DepthCamera(BasePerspectiveCamera camera) {
        this(camera, clampedDepthValues, rayDistance);
    }

    /**
     * Canonical constructor
     * @param camera
     *   The camera to monitor
     * @param mode
     *   The depth write mode
     * @param modeSupplier
     *   A function that generates a distance mode for this depth camera
     */
    public DepthCamera(BasePerspectiveCamera camera, DepthMode mode, Function<DepthCamera,DistanceMode> modeSupplier) {
        this.cameraMonitoring = camera;
        this.mode = mode;
        this.distanceCalculator = modeSupplier.apply(this);
    }

    @Override
    public int width() { return cameraMonitoring.width; }

    @Override
    public int height() { return cameraMonitoring.height; }

    @Override
    public Ray createRay(final double pixelColumn, final double pixelRow, final double time) {
        // create pinhole camera rays so the depth image is crisp and in-focus
        // TODO: code smell here, this method is 99% similar to PinHoleCamera::createRay, there can probably be an abstraction
        if (pixelColumn < 0 || pixelColumn > cameraMonitoring.width) throw new IllegalArgumentException("invalid pixel column for camera");
        if (pixelRow < 0 || pixelRow > cameraMonitoring.height) throw new IllegalArgumentException("invalid pixel row for camera");
        final var xOffset = (pixelColumn) * cameraMonitoring.pixelSize;
        final var yOffset = (pixelRow) * cameraMonitoring.pixelSize;
        final var worldX = cameraMonitoring.halfWidth - xOffset;
        final var worldY = cameraMonitoring.halfHeight - yOffset;
        final double worldZ = -cameraMonitoring.focalDistance;
        final var cameraInverse = cameraMonitoring.transform.inverse();
        final var pixel = cameraInverse.multiply(Tuple.makePoint(worldX, worldY, worldZ));
        final var origin = cameraInverse.multiply(Tuple.makePoint());
        final var direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction, time);
    }

    /**
     * An object that contains a double.
     * Used so the canvas for each lambda can write out its min max values in 'getMinMaxValues'
     */
    private static final class FloatBox { 
        public double x; 
        FloatBox(double initialValue) { this.x = initialValue; }
    }

    protected static final Pair<Double, Double> getMinMaxValues(Canvas canvas) {
        final FloatBox minDistance = new FloatBox(Double.MAX_VALUE);
        final FloatBox maxDistance = new FloatBox(Double.MIN_VALUE);
        canvas.forEach(pixel -> {
            final boolean isInfinity = Double.compare(pixel.x, Double.POSITIVE_INFINITY) == 0;
            // don't write infinity to max distance
            if (pixel.x > maxDistance.x && !isInfinity) {
                maxDistance.x = pixel.x;
            }
            if (pixel.x < minDistance.x) {
                minDistance.x = pixel.x;
            }
        });
        final var depthInfo = new Pair<>(minDistance.x, maxDistance.x);
        return depthInfo;
    }

    @Override
    public Canvas takePicture(final World world, final Canvas canvas) {
        final var rawImage = Camera.super.takePicture(world, canvas); // TODO if a wrapper camera is storing this depth camera as its internal camera, will they call this method?
        final var depthInfo = getMinMaxValues(rawImage);
        rawImage.writeAll(mode.apply(depthInfo));
        return rawImage;
    }

    @Override
    public Color pixelAt(World world, double pixelColumn, double pixelRow, double time) {
        final var ray = createRay(pixelColumn, pixelRow, time);
        final var intersections = world.intersect(ray);
        final double distance = this.distanceCalculator.distance(ray, intersections);
        return new Color(distance, distance, distance);
    }
}
