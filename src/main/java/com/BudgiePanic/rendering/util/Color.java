package com.BudgiePanic.rendering.util;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

/**
 * Color object.
 * 
 * @author BudgiePanic
 */
public class Color extends Tuple {
    
    /**
     * Create a new color with zeroed rgba components.
     */
    public Color() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Create a new color.
     * 
     * @param red
     *     The amount of red. Should be between 0 and 1.
     * @param green
     *     The amount of green. Should be between 0 and 1.
     * @param blue
     *     The amount of blue. Should be between 0 and 1.
     * @param alpha
     *     The amount of alpha. Should be between 0 and 1.
     */
    public Color(float red, float green, float blue, float alpha) {
        super(red, green, blue, alpha);
    }

    /**
     * Create a new color with alpha automatically filled.
     *
     * @param red
     *     The amount of red. Should be between 0 and 1.
     * @param green
     *     The amount of green. Should be between 0 and 1.
     * @param blue
     *     The amount of blue. Should be between 0 and 1.
     */
    public Color(float red, float green, float blue) {
        super(red, green, blue, 0f);
    }

    /**
     * Gets the amount of red in this color.
     * @return
     *     The amount of red.
     */
    public float getRed() {
        return this.x;
    }

    /**
     * Gets the amount of green in this color.
     * @return
     *     The amount of green.
     */
    public float getGreen() {
        return this.y;
    }

    /**
     * Gets the amount of blue in this color.
     * @return
     *     The amount of blue.
     */
    public float getBlue() {
        return this.z;
    }

    /**
     * Gets the amount of alpha in this color.
     * @return
     *     The amount of alpha.
     */
    public float getAlpha() {
        return this.w;
    }

}
