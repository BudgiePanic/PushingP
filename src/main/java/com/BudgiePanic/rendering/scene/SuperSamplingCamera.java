package com.BudgiePanic.rendering.scene;

import java.util.List;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * Super sampling anti-aliasing camera.
 *
 * @author BudgiePanic
 */
public class SuperSamplingCamera implements Camera {

    public static interface SampleMode {
        /**
         * Get the ray origins for a pixel, offset from [0.5, 0.5]
         * @return
         */
        List<Pair<Float, Float>> subPixelLocations();
    }

    public static final SampleMode grid = new Grid();

    private final static class Grid implements SampleMode {
        final static List<Pair<Float, Float>> pattern = List.of(
            new Pair<>(0f, 0f),
            new Pair<>(-0.25f, -0.25f),
            new Pair<>(0.25f, 0.25f),
            new Pair<>(-0.25f, 0.25f),
            new Pair<>(0.25f, -0.25f)
        );
        @Override
        public List<Pair<Float, Float>> subPixelLocations() { return pattern; }
    }

    /**
     * Internal camera that provides the actual image taking functionality
     */
    protected final Camera camera;

    /**
     * Provides the ray origin points for the rays case per pixel
     */
    protected final SampleMode mode;

    public SuperSamplingCamera(Camera camera, SampleMode mode) {
        this.camera = camera;
        this.mode = mode;
    }

    @Override
    public int width() { return camera.width(); }

    @Override
    public int height() { return camera.height(); }

    @Override
    public Ray createRay(float pixelColumn, float pixelRow, float time) {
        return camera.createRay(pixelColumn, pixelRow, time);
    }

    @Override
    public Color pixelAt(World world, float pixelColumn, float pixelRow, float time) {
        // the job supplier will make the pixel locations be: [column + 0.5, row + 0.5]
        final int numbSubPixels = mode.subPixelLocations().size();
        float red = 0, green = 0, blue = 0;
        for (final var point : mode.subPixelLocations()) {
            Color color = camera.pixelAt(world, pixelColumn + point.a(), pixelRow + point.b(), time);
            red += color.getRed(); 
            green += color.getGreen();
            blue += color.getBlue();
        }
        return new Color(red / numbSubPixels, green / numbSubPixels, blue / numbSubPixels);
    }
}
