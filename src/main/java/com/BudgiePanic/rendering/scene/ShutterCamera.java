package com.BudgiePanic.rendering.scene;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * The shutter camera takes an image with an exposure time.
 * Multiple rays are cast per pixel, at different times, and the resulting colors are averaged together.
 * Shapes in motion will cause motion blur to appear with shutter camera for long exposure times.
 * 
 * @author BudgiePanic
 */
public class ShutterCamera implements Camera {

    /**
     * How to combine the pixels colors collected over the course of the image exposure.
     */
    public static sealed interface ExposureMode permits Averaged, Summed {
        Color process(Color[] colors, int raysPerExposure);
    }

    
    private static final class Averaged implements ExposureMode {
        static final Averaged instance = new Averaged();
        @Override
        public Color process(Color[] colors, int raysPerExposure) {
            double red = 0;
            double green = 0;
            double blue = 0;
            // cast n rays into the scene for this pixel 
            // and then average the colors from the rays together
            for (final var color : colors) {
                red += color.getRed() * color.getRed(); 
                green += color.getGreen() * color.getGreen(); 
                blue += color.getBlue() * color.getBlue(); 
            }
            // average the squared colors
            red = Math.sqrt(red / raysPerExposure); 
            green = Math.sqrt(green / raysPerExposure); 
            blue = Math.sqrt(blue / raysPerExposure); 
            return new Color(red, green, blue);
        }
    }
    
    private static final class Summed implements ExposureMode {
        static final Summed instance = new Summed();
        @Override
        public Color process(Color[] colors, int raysPerExposure) {
            double red = 0;
            double green = 0;
            double blue = 0;
            for (var color : colors) {
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
            }
            return new Color(red, green, blue);
        }
    }
    /**
     * colors collected over the course of the image exposure are averaged together.
     */
    public static final ExposureMode averaged = Averaged.instance;

    /**
     * Colors collected over the course of the image exposure are summed together.
     */
    public static final ExposureMode summed = Summed.instance;

    /**
     * The recommended default exposure mode.
     */
    public static final ExposureMode defaultExposureMode = averaged;

    /**
     * The recommended default standard exposure time.
     */
    public static final double defaultExposureTime = 1.0/60.0;

    /**
     * The recommended default long exposure time.
     */
    public static final double defaultLongExposureTime = 1.0/2.0;

    /**
     * The recommended default short exposure time.
     */
    public static final double defaultShortExposureTIme = 1.0/500.0;

    /**
     * The recommended number of rays to cast per pixel over the course of the exposure.
     */
    public static final int defaultRaysPerExposure = 20;

    /**
     * The internal camera
     */
    protected final BasePerspectiveCamera camera;

    /**
     * How long the image exposure is.
     */
    protected final double exposureDuration;

    /**
     * How many rays to cast per pixel over the course of the exposure.
     * Ray timings will be evenly distributed from exposure start to exposure end.
     */
    protected final int raysPerExposure;

    /**
     * Delegate object to combine the colors collected for each pixel over the course of the image exposure.
     */
    protected final ExposureMode mode;


    public ShutterCamera(BasePerspectiveCamera camera, double exposureDuration, int raysPerExposure, ExposureMode mode) {
        this.camera = camera;
        this.exposureDuration = exposureDuration;
        this.raysPerExposure = raysPerExposure;
        this.mode = mode;
    }

    @Override
    public int width() { return this.camera.width; }

    @Override
    public int height() { return this.camera.height; }

    @Override
    public Ray createRay(double pixelColumn, double pixelRow, double time) { return camera.createRay(pixelColumn, pixelRow, time); }

    @Override
    public Color pixelExposureAt(World world, double pixelColumn, double pixelRow) {
        // overriding base camera pixelAt behaviour feels a bit ugly, but it least it works.
        // find the times of each ray cast
        // the times need to be evenly distributed from 0s to exposureDuration
        final Color[] samples = new Color[raysPerExposure];
        for (int i = 0; i < raysPerExposure; i++) {
            final var time = (exposureDuration / raysPerExposure) * i;
            final var color = pixelAt(world, pixelColumn, pixelRow, time);
            samples[i] = color;
        }
        final Color color = this.mode.process(samples, raysPerExposure);
        return color; 
    }

    @Override
    public Color pixelAt(World world, double pixelColumn, double pixelRow, double time) {
        return camera.pixelAt(world, pixelColumn, pixelRow, time);
    }
    
}
