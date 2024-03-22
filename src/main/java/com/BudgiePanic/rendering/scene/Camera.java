package com.BudgiePanic.rendering.scene;

import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * A camera takes an image of a world and outputs the image to a canvas.
 * 
 * @author BudgiePanic
 */
public interface Camera {
    
    /**
     * Get the number of horizontal pixels of the camera (columns).
     * @return
     *   The number of horizontal pixels of the camera.
     */
    int width();

    /**
     * Get the number of vertical pixels of the camera (rows).
     * @return
     *   The number of vertical pixels of the camera.
     */
    int height();

    /**
     * Create a ray that goes through a specific pixel of the camera.
     *
     * @param pixelColumn
     *   The x column of the pixel.
     * @param pixelRow
     *   The y row of the pixel.
     * @param time
     *   The time the ray was created since the image exposure started.
     * @return
     *    A ray with time 'time' that passes through (col, row) pixel of the camera from the camera origin.
     */
    Ray createRay(int pixelColumn, int pixelRow, float time);

    /**
     * Create a ray that goes through the specified pixel of the camera at time zero.
     *
     * @param pixelColumn
     *   The x column of the pixel.
     * @param pixelRow
     *   The y row of the pixel.
     * @return
     *   A ray that passes through (col, row) pixel of the camera from the camera origin. 
     */
    default Ray createRay(int pixelColumn, int pixelRow) { return createRay(pixelColumn, pixelRow, 0f); }

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
    Canvas takePicture(World world, Canvas canvas);
    
    /**
     * Cast rays out of the camera into the scene. Fills a new canvas with colors from the rays.
     *
     * @param world
     *   The world to take an image of.
     * @return
     *   An image of the world taken from the camera's perspective.
     */
    default Canvas takePicture(World world) {
        return takePicture(world, new ArrayCanvas(width(), height()));
    }
}
