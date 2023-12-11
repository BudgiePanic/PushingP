package com.BudgiePanic.rendering.toy;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Runs a simple projectile simulation.
 * Writes the projectiles trajectory to a canvas, which is then saved to a PPM
 * file for viewing.
 * 
 * @author BudgiePanic
 */
public final class Artillery implements Runnable{

    private Tuple proj;
    private Tuple vel;
    private Tuple gravity;
    private Tuple wind;
    private Canvas canvas;
    private static final int maxIterations = 2000000;
    private static final String fileName = "image.ppm";

    public Artillery(Tuple projectilePosition, Tuple projectileVelocity, Tuple gravity, Tuple wind) {
        this.proj = projectilePosition;
        this.vel = projectileVelocity;
        this.gravity = gravity;
        this.wind = wind;
        if (gravity.y <= 0f) gravity = new Tuple(gravity.x, -9.8f, gravity.z, gravity.w);
        this.canvas = new ArrayCanvas(500, 500);
    }

    /**
     * Runs the physics simulation for one iteration.
     * 
     * @param delta
     *     The amount of time since the last tick.
     */
    private void tick(float delta) {
        proj = proj.add(vel.multiply(delta));
        vel = vel.add(gravity.multiply(delta)).add(wind.multiply(delta));
    }

    @Override
    public void run() {
        final Color projColor = Colors.white;
        final int canvasHeight = canvas.getHeight();
        final int canvasWidth = canvas.getWidth();
        int iterations = 0;
        int pixelsWritten = 0;
        while (proj.y > 0f && iterations < maxIterations) {
            // Write the position of the projectile to the canvas
            int projX = (int) proj.x;
            int projY = canvasHeight - (int) proj.y;
            if (
                projX >= 0 &&
                projX < canvasWidth &&
                projY >= 0 &&
                projY < canvasHeight
            ) {
                canvas.writePixel(projX, projY, projColor);
                pixelsWritten++;
            }
            // run the simulation by one tick
            tick(0.1f);
            iterations++;
        }
        // write the canvas to disc for viewing
        var lines = CanvasWriter.canvasToPPMString(canvas);
        File file = new File(System.getProperty("user.dir"), fileName);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println(String.format("Done, wrote %d pixels to canvas.", pixelsWritten));
    }  
}
