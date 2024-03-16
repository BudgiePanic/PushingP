package com.BudgiePanic.rendering.toy;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.reporting.ProgressWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;

/**
 * Common functionality in most ray tracer demos.
 * 
 * @author BudgiePanic
 */
public abstract class BaseDemo implements Runnable {
    
    protected final String fileName;
    protected final Camera camera;

    public BaseDemo() {
        this.fileName = getName();
        this.camera = getCamera();
    }

    /**
     * Get the name of the image file produced by this demo.
     *
     * @return
     *     The name of the image file produced by this demo.
     */
    protected abstract String getName();

    /**
     * Get the camera used to image this demo scene.
     *
     * @return
     *   The camera used to image this demo scene.
     */
    protected abstract Camera getCamera();

    /**
     * Assemble the world that contains the content highlighted by this demo.
     *
     * @return
     *     The world to take an image of.
     */
    protected abstract World createWorld();

    private Canvas imageWorld() {
        System.out.println("INFO: taking picture");
        var world = createWorld();
        var canvas = camera.takePicture(world, new ProgressWrapper(new ArrayCanvas(camera.width, camera.height), 20));
        return canvas;
    }

    protected static void saveImageToFile(Canvas canvas, String fileName) {
        System.out.println("INFO: saving image");
        var lines = CanvasWriter.canvasToPPMString(canvas);
        var file = new File(System.getProperty("user.dir"), fileName);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
        System.out.println("INFO: saved image");
        System.out.println("INFO: done");
    }

    @Override
    public void run() {
        var pixels = imageWorld();
        saveImageToFile(pixels, fileName);
    }
    
}
