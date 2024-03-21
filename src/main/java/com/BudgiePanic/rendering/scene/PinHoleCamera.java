package com.BudgiePanic.rendering.scene;

import java.util.ArrayList;
import java.util.List;

import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * The camera allows the world to be viewed from different perspectives by abstracting the transform complexity from the user. 
 * 
 * @author BudgiePanic
 */
public class PinHoleCamera {
    
    /**
     * The width of the camera's far plane in pixels.
     */
    public final int width;

    /**
     * The height of the camera's far plane in pixels.
     */
    public final int height;

    /**
     * The field of view of the camera in radians.
     */
    protected final float fov;

    /**
     * Transform to go to camera space.
     * Can move an object in the world relative to the camera which lies at [0,0,0]
     */
    protected final Matrix4 transform;

    /**
     * The world space size of the pixel. 
     * Pixel width == pixel height because pixels are square.
     */
    protected final float pixelSize;
    protected final float halfHeight;
    protected final float halfWidth;

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
        this.width = width;
        this.height = height;
        this.fov = fov;
        this.transform = transform;
        // determine pixel size
        float halfView = (float) Math.tan(fov/2.0);
        float aspect = ((float) width / (float) height);
        // assuming square pixels, so pixel width == pixel height
        this.halfWidth = (aspect >= 1f) ? halfView : halfView * aspect; 
        this.halfHeight = (aspect >= 1f) ? halfView / aspect : halfView;
        this.pixelSize = (halfWidth * 2.0f) / width;
    }

    /**
     * Create a ray that goes through the specified pixel of the camera.
     *
     * @param pixelColumn
     *   The x column of the pixel.
     * @param pixelRow
     *   The y row of the pixel.
     * @return
     *   A ray that passes through (col, row) pixel of the camera from the camera origin. 
     */
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
        final float worldZ = -1;
        var pixel = cameraInverse.multiply(Tuple.makePoint(worldX, worldY, worldZ));
        var origin = cameraInverse.multiply(Tuple.makePoint());
        var direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction);
    }

    /**
     * Cast rays out of the camera into the scene. Fills a new canvas with colors from the rays.
     *
     * @param world
     *   The world to take an image of.
     * @return
     *   An image of the world taken from the camera's perspective.
     */
    public Canvas takePicture(World world) {
        if (world == null) throw new IllegalArgumentException("world is null");
        return takePicture(world, new ArrayCanvas(width, height));
    }

    /**
     * Cast rays out of the camera into the scene. Overwrites the canvas with colors from the rays.
     * 
     * @param world
     *   The world to take an image of.
     * @param canvas
     *   The canvas to write the colors to.
     * @return
     *   An image of the world taken from the camera's perspective.
     */
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
