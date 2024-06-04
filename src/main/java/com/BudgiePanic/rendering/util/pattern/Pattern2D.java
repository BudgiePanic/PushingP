package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.io.CanvasReader;
import com.BudgiePanic.rendering.io.CanvasReader.ParsingException;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.FloatHelp;

/**
 * Two dimensional pattern variant for use in uv texture mapping.
 *
 * @author BudgiePanic
 */
public interface Pattern2D {

    /**
     * Create a checker pattern.
     * 
     * @param width
     *   The number of checker squares in the u axis.
     * @param height
     *   The number of checker squares in the v axis.
     * @param a
     *   The first pattern to sample.
     * @param b
     *   The second pattern to sample.
     * @return
     *   A pattern the alternates between a and b.
     */
    public static Pattern2D checker(final double width, final double height, final Pattern2D a, final Pattern2D b) {
        return (u, v) -> {
            double x = Math.floor(u * width);
            double y = Math.floor(v * height);
            return FloatHelp.compareFloat((x + y) % 2, 0.0) == 0 ? a.sample(u, v) : b.sample(u, v); 
        };
    }
    
    /**
     * Create a solid color pattern.
     * 
     * @param color
     *   The color of the pattern.
     * @return
     *   A pattern that always returns the color when sampled.
     */
    public static Pattern2D solidColor(final Color color) { return (u,v) -> { return color; }; }

    /**
     * Verification pattern used to check if a shape TextureMap object is working correctly.
     * @param background
     *   The background color of the pattern
     * @param upperLeft
     *   The color in the upper left corner
     * @param upperRight
     *   The color in the upper right corner
     * @param lowerLeft
     *   The color in the lower left corner
     * @param lowerRight
     *   The color in the lower right corner
     * @return
     *   A pattern with different colors in each corner
     */
    public static Pattern2D mapCheck(Color background, Color upperLeft, Color upperRight, Color lowerLeft, Color lowerRight) { 
        return (final double u, final double v) -> {
            if (v > 0.8) {
                if (u < 0.2) { return upperLeft; }
                if (u > 0.8) { return upperRight; }
            } else if (v < 0.2) {
                if (u < 0.2) { return lowerLeft; }
                if (u > 0.8) { return lowerRight; }
            }
            return background;
        }; 
    }

    /**
     * Load a new texture 2D pattern from the local directory.
     * If multiple texture map patterns are sharing the same texture2D, consider aliasing the pattern2D across them to save space.
     * @param fileName
     *   The name of the texture file in the local directory
     * @return
     *   A texture2D pattern that samples the texture image.
     */
    public static Pattern2D texture2D (final String fileName) {
        try {
            return new Texture2D(CanvasReader.createCanvas(fileName));
        } catch (ParsingException e) {
            System.out.print("WARN: could not create texture from image " + fileName + "\":");
            System.out.println(e.getLocalizedMessage());
            System.out.println("WARN: falling back to checker pattern");
            return checker(10, 10, solidColor(Colors.black), solidColor(Colors.red.add(Colors.blue)));
        }
    }

    /**
     * Sample the 2D pattern.
     *
     * @param u
     *   The u coordinate in pattern space.
     * @param v
     *   The v coordinate in pattern space.
     * @return
     *   The color of the pattern at (u,v).
     */
    public Color sample(double u, double v);
    
}
