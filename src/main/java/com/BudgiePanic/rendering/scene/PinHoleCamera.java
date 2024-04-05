package com.BudgiePanic.rendering.scene;

import com.BudgiePanic.rendering.util.Color;
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
    public Ray createRay(float pixelColumn, float pixelRow, float time) {
        // pre condition checks
        if (pixelColumn < 0 || pixelColumn > this.width) throw new IllegalArgumentException("invalid pixel column for camera");
        if (pixelRow < 0 || pixelRow > this.height) throw new IllegalArgumentException("invalid pixel row for camera");
        // compute the offset from the edge of the canvas to the center of the pixel
        final var xOffset = (pixelColumn) * this.pixelSize;
        final var yOffset = (pixelRow) * this.pixelSize;
        // coordinates of the pixel in world space, LHS coordinate system
        // this means that from the camera's point of view the +ve x direction is to the left.
        // the 'camera' looks at -ve z direction, origin at [0,0,0] and the transform moves objects in the world about the camera, instead of moving the camera in the world.
        // assuming the camera view plane is normalized, the far plane is at z = -1 and there is no near plane (camera view is a pyramid shape)
        final var worldX = this.halfWidth - xOffset;
        final var worldY = this.halfHeight - yOffset;
        final float worldZ = -focalDistance;
        // move this 'camera space' ray into world space
        final var cameraInverse = this.transform.inverse();
        final var pixel = cameraInverse.multiply(Tuple.makePoint(worldX, worldY, worldZ));
        final var origin = cameraInverse.multiply(Tuple.makePoint());
        final var direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction, time);
    }

    @Override
    public Color pixelAt(World world, float pixelColumn, float pixelRow, float time) {
        return world.computeColor(createRay(pixelColumn, pixelRow, time));
    }

}
