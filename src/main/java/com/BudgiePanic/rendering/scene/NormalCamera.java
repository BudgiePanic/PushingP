package com.BudgiePanic.rendering.scene;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import java.util.List;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * The normal camera records the normals of shapes that are struck by rays cast from the camera monitoring.
 * Rays that cause no intersections will write BLACK to the normal map.
 *
 * @author BudgiePanic
 */
public class NormalCamera implements Camera {

    /**
     * The camera being monitored.
     * The normal camera will copy the monitoring camera's settings when taking the normal map image.
     */
    protected final BasePerspectiveCamera cameraMonitoring;

    /**
     * Canonical constructor. Create a new Normal Camera.
     *
     * @param camera
     *   The camera to monitor.
     */
    public NormalCamera(BasePerspectiveCamera camera) {
        this.cameraMonitoring = camera;
    }

    @Override
    public int width() { return cameraMonitoring.width; }

    @Override
    public int height() { return cameraMonitoring.height; }

    @Override
    public Ray createRay(int pixelColumn, int pixelRow) {
        // create pinhole camera rays so the normal map image is crisp and in-focus
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
    public Canvas takePicture(World world, Canvas canvas) {
        if (canvas == null || canvas.getHeight() < cameraMonitoring.height || canvas.getWidth() < cameraMonitoring.width) throw new IllegalArgumentException();
        List<Pair<Integer, Integer>> jobs = cameraMonitoring.generateJobs();
        // intersect each pixel-ray with the world and record the normal of the shape that was intersected with
        jobs.parallelStream().forEach(pixel -> {
            final int column = pixel.a();
            final int row = pixel.b();
            final var ray = createRay(column, row);
            final var intersections = world.intersect(ray);
            final Tuple normal = intersections.flatMap(Intersection::Hit).map(i -> i.computeNormal(ray)).orElseGet(() -> makeVector());
            canvas.writePixel(column, row, new Color(normal.x, normal.y, normal.z));
        }); 
        return canvas;
    }
}
