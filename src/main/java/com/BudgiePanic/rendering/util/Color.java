/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
    public Color(double red, double green, double blue, double alpha) {
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
    public Color(double red, double green, double blue) {
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
    public double getRed() {
        return this.x;
    }

    /**
     * Gets the amount of green in this color.
     * @return
     *     The amount of green.
     */
    public double getGreen() {
        return this.y;
    }

    /**
     * Gets the amount of blue in this color.
     * @return
     *     The amount of blue.
     */
    public double getBlue() {
        return this.z;
    }

    /**
     * Gets the amount of alpha in this color.
     * @return
     *     The amount of alpha.
     */
    public double getAlpha() {
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

    public Color multiply(double value) {
        return new Color(super.multiply(value));
    }

    public Color add(double red, double green, double blue) {
        return new Color(super.add(red, green, blue));
    }

    public Color add(double red, double green, double blue, double alpha) {
        return new Color(super.add(red, green, blue, alpha));
    }

    public Color subtract(double red, double green, double blue) {
        return new Color(super.subtract(red, green, blue));
    }

    public Color subtract(double red, double green, double blue, double alpha) {
        return new Color(super.subtract(red, green, blue, alpha));
    }

    /**
     * Component wise multiplication of color components.
     * 
     * @param other
     *     The other color.
     * @return
     *     The Hadamard product of 'this' and 'other'.
     */
    public Color colorMul(Color other) {
        return new Color(
            this.getRed() * other.getRed(),
            this.getGreen() * other.getGreen(),
            this.getBlue() * other.getBlue()
        );
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
               // comparedouble(this.getAlpha(), otherColor.getAlpha()); // Currently ignoring alpha at this stage.
    }

}
