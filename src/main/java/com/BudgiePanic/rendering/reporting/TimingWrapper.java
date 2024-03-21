package com.BudgiePanic.rendering.reporting;

import com.BudgiePanic.rendering.scene.PinHoleCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Wrapper class to non-intrusively measure the time it takes for a camera to take an image of a world.
 * @author BudgiePanic
 */
public class TimingWrapper extends PinHoleCamera {

    // TODO: extract camera interface, hold a camera instance, instead of extending camera.
    //       using a wrapper that holds a camera instance is a better design than extending camera...

    public TimingWrapper(int width, int height, float fov, Matrix4 transform) {
        super(width, height, fov, transform);
    }

    @Override
    public Canvas takePicture(World world, Canvas canvas) {
        System.out.println("INFO: timing image capture duration of " + world + " to " + canvas + " by camera " + this);
        var startTime = System.currentTimeMillis();
        var result = super.takePicture(world, canvas);
        var endTime = System.currentTimeMillis();
        System.out.println("INFO: image of " + world + " via " + this.toString() + " took " + (endTime - startTime) + " milliseconds to complete.");
        return result;
    }
    
}
