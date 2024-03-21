package com.BudgiePanic.rendering.reporting;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * Wrapper class to non-intrusively measure the time it takes for a camera to take an image of a world.
 * @author BudgiePanic
 */
public class TimingWrapper implements Camera {

    /**
     * The internal camera instance.
     */
    protected final Camera internal;

    /**
     * Create a new camera timing wrapper
     * @param camera
     *   The camera to measure the performance of.
     */
    public TimingWrapper(Camera camera) { this.internal = camera; }

    @Override
    public Canvas takePicture(World world, Canvas canvas) {
        System.out.println("INFO: timing image capture duration of " + world + " to " + canvas + " by camera " + this);
        var startTime = System.currentTimeMillis();
        var result = internal.takePicture(world, canvas);
        var endTime = System.currentTimeMillis();
        System.out.println("INFO: image of " + world + " via " + this.toString() + " took " + (endTime - startTime) + " milliseconds to complete.");
        return result;
    }

    @Override
    public int width() { return internal.width(); }

    @Override
    public int height() { return internal.height(); }

    @Override
    public Ray createRay(int pixelColumn, int pixelRow) { return internal.createRay(pixelColumn, pixelRow); }
    
}
