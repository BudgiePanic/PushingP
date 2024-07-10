/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.scene;

import java.util.ArrayList;
import java.util.List;

import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;
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
    Ray createRay(double pixelColumn, double pixelRow, double time);

    /**
     * Creates a list of pixels that can be used as indices in a stream.
     * @return
     *   A list of all the rows and columns that can be imaged by this camera.
     */
    default List<Pair<Integer, Integer>> generateJobs() {
        List<Pair<Integer, Integer>> jobs = new ArrayList<>(this.height() * this.width());
        for (int row = 0; row < this.height(); row++) {
            for (int col = 0; col < this.width(); col++) {
                jobs.add(new Pair<Integer,Integer>(col, row));
            }
        }
        return jobs;
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
    default Canvas takePicture(World world, Canvas canvas) {
        // pre condition check: is the canvas big enough for the camera?
        if (canvas == null || canvas.getHeight() < this.height() || canvas.getWidth() < this.width()) throw new IllegalArgumentException();
        List<Pair<Integer, Integer>> jobs = generateJobs();
        jobs.parallelStream().forEach(pixel -> canvas.writePixel(pixel.a(), pixel.b(), pixelExposureAt(world, pixel.a(), pixel.b())));
        return canvas;
    }

    /**
     * Get the color for pixel [column, row] taken by this camera when imaging a world.
     *
     * @param world
     *   The world that should be imaged by the camera.
     * @param pixelColumn
     *   The x column of the pixel.
     * @param pixelRow
     *   The y row of the pixel.
     * @param time
     *   The time during the exposure when the pixel value was sampled.
     * @return
     *   The color at pixel [column, row]
     */
    Color pixelAt(World world, double pixelColumn, double pixelRow, double time);

    /**
     * Get the color for pixel [column, row] taken by this camera for the entire exposure when imaging a world.
     * Camera implementations may wish to override this method with their own behaviour.
     * By default the pixel value comes from an instantaneous exposure taken at time = zero.
     * 
     * @param world 
     *   The world being images by the camera.
     * @param pixelColumn
     *   The x column of the pixel.
     * @param pixelRow
     *   The y row of the pixel.
     * @return
     *   The sampled color at pixel [column, row] from the image exposure.
     */
    default Color pixelExposureAt(World world, double pixelColumn, double pixelRow) { return pixelAt(world, pixelColumn, pixelRow, 0.0); }

    /**
     * Get the color for pixel [column, row] taken by this camera for the entire exposure when imaging a world.
     * 
     * Convenience method, automatically adds offset to center the sample point within the pixel.
     * 
     * @param world
     *   The world being images by the camera.
     * @param pixelColumn
     *   The discrete column of the pixel.
     * @param pixelRow
     *   The y row of the pixel.
     * @return 
     *   The sampled color at pixel [column, row] from the image exposure.
     */
    default Color pixelExposureAt(World world, int pixelColumn, int pixelRow) { return pixelExposureAt(world, pixelColumn + 0.5, pixelRow + 0.5); }
    
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
