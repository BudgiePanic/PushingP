package com.BudgiePanic.rendering.scene;

import java.util.List;

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

    protected final BasePerspectiveCamera cameraMonitoring;

    public DepthCamera(BasePerspectiveCamera camera) {
        this.cameraMonitoring = camera;
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
    public Canvas takePicture(final World world, final Canvas canvas) {
        if (canvas == null || canvas.getHeight() < cameraMonitoring.height || canvas.getWidth() < cameraMonitoring.width) throw new IllegalArgumentException();
        List<Pair<Integer, Integer>> jobs = cameraMonitoring.generateJobs();
        // TODO code smell here: all camera's follow this same recipe for taking a picture, the only thing that changes is the consumer used in the stream
        // TODO                  so this method can likely be pulled up, and concrete classes need only supply the consumer
        jobs.parallelStream().forEach(pixel -> {
            final int column = pixel.a();
            final int row = pixel.b();
            final var ray = createRay(column, row);
            final var intersections = world.intersect(ray);
            final var distance = intersections.flatMap(Intersection::Hit).map(Intersection::a).orElse(Float.POSITIVE_INFINITY);
            final var inverseDistance = 1f / distance;
            canvas.writePixel(column, row, new Color(inverseDistance, inverseDistance, inverseDistance));
        });
        return canvas;
    }
    
}
