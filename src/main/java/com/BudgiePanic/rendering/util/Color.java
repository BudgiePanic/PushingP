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
     * Copy constructor. Makes a color from a Tuple.
     * 
     * @param other
     *     The tuple being copied.
     */
    public Color(Tuple other) {
        super(other);
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

    /**
     * Add method override to maintain color type. 
     * I have a feeling this set up is going to run the garbage collector hard.
     */
    public Color add(Tuple other) {
        return new Color(super.add(other));
    }

    public Color subtract(Tuple other) {
        return new Color(super.subtract(other));
    }

    public Color multiply(float value) {
        return new Color(super.multiply(value));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other){
            return true;
        }
        if (other == null || this.getClass() != other.getClass()){
            return false;
        }
        Color otherColor = (Color) other;
        return compareFloat(this.getRed(), otherColor.getRed()) == 0 &&
               compareFloat(this.getGreen(), otherColor.getGreen()) == 0 &&
               compareFloat(this.getBlue(), otherColor.getBlue()) == 0; // &&
               // compareFloat(this.getAlpha(), otherColor.getAlpha()); // Currently ignoring alpha at this stage.
    }

}
