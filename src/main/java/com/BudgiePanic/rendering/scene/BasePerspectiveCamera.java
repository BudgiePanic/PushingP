package com.BudgiePanic.rendering.scene;

import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Base perspective camera contains the fields used by perspective concrete cameras.
 * @author BudgiePanic
 */
public abstract class BasePerspectiveCamera implements Camera {
    /**
     * The number of columns on the camera's imaging plane.
     */
    public final int width;
    /**
     * The number of rows on the camera's imaging plane.
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
     * The distance of the camera's imaging plane to the aperture. 
     * Affects the final image FOV as more distance constrains which rays can fit through the apeture, narrowing the image.
     */
    protected final float focalDistance;

    /**
     * Create a new Base perspective camera.
     * @param width
     *   The horizontal size of the camera in pixels.
     * @param height
     *   The vertical size of the camera in pixes.
     * @param fov
     *   The field of view of the camera in radians.
     * @param transform
     *   The camera transform.
     */
    public BasePerspectiveCamera(int width, int height, float fov, float focalDistance, Matrix4 transform) {
        this.width = width;
        this.height = height;
        this.fov = fov;
        this.focalDistance = focalDistance;
        this.transform = transform;
        // determine pixel size
        float halfView = (float) Math.tan(fov/2.0);
        float aspect = ((float) width / (float) height);
        // assuming square pixels, so pixel width == pixel height
        this.halfWidth = (aspect >= 1f) ? halfView : halfView * aspect; 
        this.halfHeight = (aspect >= 1f) ? halfView / aspect : halfView;
        this.pixelSize = (halfWidth * 2.0f) / width;
    }

    @Override
    public int width() { return this.width; }

    @Override
    public int height() { return this.height; }
}
