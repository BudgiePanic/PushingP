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
            float red = 0;
            float green = 0;
            float blue = 0;
            // cast n rays into the scene for this pixel 
            // and then average the colors from the rays together
            for (final var color : colors) {
                red += color.getRed() * color.getRed(); 
                green += color.getGreen() * color.getGreen(); 
                blue += color.getBlue() * color.getBlue(); 
            }
            // average the squared colors
            red = (float) Math.sqrt(red / raysPerExposure); 
            green = (float) Math.sqrt(green / raysPerExposure); 
            blue = (float) Math.sqrt(blue / raysPerExposure); 
            return new Color(red, green, blue);
        }
    }
    
    private static final class Summed implements ExposureMode {
        static final Summed instance = new Summed();
        @Override
        public Color process(Color[] colors, int raysPerExposure) {
            float red = 0;
            float green = 0;
            float blue = 0;
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
    public static final float defaultExposureTime = 1f/60f;

    /**
     * The recommended default long exposure time.
     */
    public static final float defaultLongExposureTime = 1f/2f;

    /**
     * The recommended default short exposure time.
     */
    public static final float defaultShortExposureTIme = 1f/500f;

    /**
     * The recommended number of rays to cast per pixel over the course of the exposure.
     */
    public static final int defaultRaysPerExposure = 5;

    /**
     * The internal camera
     */
    protected final BasePerspectiveCamera camera;

    /**
     * How long the image exposure is.
     */
    protected final float exposureDuration;

    /**
     * How many rays to cast per pixel over the course of the exposure.
     * Ray timings will be evenly distributed from exposure start to exposure end.
     */
    protected final int raysPerExposure;

    /**
     * Delegate object to combine the colors collected for each pixel over the course of the image exposure.
     */
    protected final ExposureMode mode;


    public ShutterCamera(BasePerspectiveCamera camera, float exposureDuration, int raysPerExposure, ExposureMode mode) {
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
    public Ray createRay(int pixelColumn, int pixelRow, float time) { return camera.createRay(pixelColumn, pixelRow, time); }

    @Override
    public Color pixelAt(World world, int pixelColumn, int pixelRow) {
        // overriding base camera pixelAt behaviour feels a bit ugly, but it least it works.
        // find the times of each ray cast
        // the times need to be evenly distributed from 0s to exposureDuration
        final Color[] samples = new Color[raysPerExposure];
        for (int i = 0; i < raysPerExposure; i++) {
            final var time = (exposureDuration / raysPerExposure) * i;
            final var ray = createRay(pixelColumn, pixelRow, time);
            final var color = world.computeColor(ray);
            samples[i] = color;
        }
        final Color color = this.mode.process(samples, raysPerExposure);
        return color; 
    }

    @Override
    public Color pixelAt(World world, int pixelColumn, int pixelRow, float time) {
        return camera.pixelAt(world, pixelColumn, pixelRow, time);
    }
    
}
