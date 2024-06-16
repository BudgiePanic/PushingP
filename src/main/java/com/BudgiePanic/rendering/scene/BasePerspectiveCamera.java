package com.BudgiePanic.rendering.scene;

import com.BudgiePanic.rendering.util.Tuple;
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
    protected final double fov;

    /**
     * Transform to go to camera space.
     * Can move an object in the world relative to the camera which lies at [0,0,0]
     */
    protected final Matrix4 transform;

    /**
     * The world space size of the pixel. 
     * Pixel width == pixel height because pixels are square.
     */
    protected final double pixelSize;
    protected final double halfHeight;
    protected final double halfWidth;

    /**
     * The distance of the camera's imaging plane to the aperture. 
     * Affects the final image FOV as more distance constrains which rays can fit through the apeture, narrowing the image.
     */
    protected final double focalDistance;

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
    public BasePerspectiveCamera(int width, int height, double fov, double focalDistance, Matrix4 transform) {
        this.width = width;
        this.height = height;
        this.fov = fov;
        this.focalDistance = focalDistance;
        this.transform = transform;
        // determine pixel size
        double halfView = Math.tan(fov/2.0);
        double aspect = ((double)width / (double)height);
        // assuming square pixels, so pixel width == pixel height
        this.halfWidth = (aspect >= 1.0) ? halfView : halfView * aspect; 
        this.halfHeight = (aspect >= 1.0) ? halfView / aspect : halfView;
        this.pixelSize = (halfWidth * 2.0) / width;
    }

    @Override
    public int width() { return this.width; }

    @Override
    public int height() { return this.height; }

    /**
     * project a 3D point in world space to a pixel on this camera
     * @param worldPoint
     *   The point in world space
     * @return
     *   An array of size two. The [column:x, row:y] of the pixel the point was projected onto. 
     */
    public final int[] project(Tuple worldPoint) {
        // sources: 
        //     + https://gabrielgambetta.com/computer-graphics-from-scratch/01-common-concepts.html
        //     + https://gabrielgambetta.com/computer-graphics-from-scratch/09-perspective-projection.html 
        final Tuple localPoint = this.transform.multiply(worldPoint); 
        // "The Ray Tracer Challenge" camera looks in the -ve z direction in camera space
        // but the rasterizer camera in "Computer Graphics From Scratch" faces the +ve z direction.
        // To account for this fact, we need to negate the focal distance.
        final double projx = (localPoint.x * -focalDistance) / localPoint.z;
        final double projy = (localPoint.y * -focalDistance) / localPoint.z;
        final double screenx = (projx * width) / (2 * halfWidth);
        final double screeny = (projy * height) / (2 * halfHeight);
        // projected points are currently in normalized screen space. Convert them to pixel space.
        final int col = (width / 2) + (int) Math.round(screenx);
        final int row = (height / 2) + (int) Math.round(screeny);
        return new int[] {col, row};
    }
}

