package com.BudgiePanic.rendering.scene;

import java.util.ArrayList;
import java.util.List;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * The camera allows the world to be viewed from different perspectives by abstracting the transform complexity from the user. 
 * Pinhole camera has a fixed focal distance of 1 and a small aperture of 1 ray in size.
 * 
 * @author BudgiePanic
 */
public class PinHoleCamera extends BasePerspectiveCamera {
    
    protected static final float focalDistance = 1f;

    /**
     * Create a new perspective camera. 
     * NOTE: may create orthographic camera in the future?
     *
     * @param width
     *   The horizontal size of the camera in pixels.
     * @param height
     *   The vertical size of the camera in pixes.
     * @param fov
     *   The field of view of the camera in radians.
     * @param transform
     *   The camera transform.
     */
    public PinHoleCamera(int width, int height, float fov, Matrix4 transform) {
        super(width, height, fov, focalDistance,transform);
    }

    @Override
    public Ray createRay (int pixelColumn, int pixelRow) {
        // pre condition checks
        if (pixelColumn < 0 || pixelColumn > this.width) throw new IllegalArgumentException("invalid pixel column for camera");
        if (pixelRow < 0 || pixelRow > this.height) throw new IllegalArgumentException("invalid pixel row for camera");
        // compute the offset from the edge of the canvas to the center of the pixel
        var xOffset = (pixelColumn + 0.5f) * this.pixelSize;
        var yOffset = (pixelRow + 0.5f) * this.pixelSize;
        // coordinates of the pixel in world space, LHS coordinate system
        // this means that from the camera's point of view the +ve x direction is to the left.
        // the 'camera' looks at -ve z direction, origin at [0,0,0] and the transform moves objects in the world about the camera, instead of moving the camera in the world.
        // assuming the camera view plane is normalized, the far plane is at z = -1 and there is no near plane (camera view is a pyramid shape)
        var worldX = this.halfWidth - xOffset;
        var worldY = this.halfHeight - yOffset;
        // move this 'camera space' ray into world space
        var cameraInverse = this.transform.inverse();
        final float worldZ = -focalDistance;
        var pixel = cameraInverse.multiply(Tuple.makePoint(worldX, worldY, worldZ));
        var origin = cameraInverse.multiply(Tuple.makePoint());
        var direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction);
    }

    @Override
    public Canvas takePicture(World world, Canvas canvas) {
        // pre condition check: is the canvas big enough for the camera?
        if (canvas == null || canvas.getHeight() < this.height || canvas.getWidth() < this.width) throw new IllegalArgumentException();
        List<Pair<Integer, Integer>> jobs = new ArrayList<>(this.height * this.width);
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                // canvas.writePixel(col, row, world.computeColor(createRay(col, row)));
                jobs.add(new Pair<Integer,Integer>(col, row));
            }
        }
        jobs.parallelStream().forEach(pixel -> canvas.writePixel(pixel.a(), pixel.b(), world.computeColor(createRay(pixel.a(), pixel.b()))));
        return canvas;
    }

}
