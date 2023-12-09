package com.BudgiePanic.rendering.util;

public class Color extends Tuple {
    
    public Color() {
        super(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public Color(float red, float green, float blue, float alpha) {
        super(red, green, blue, alpha);
    }

    public Color(float red, float green, float blue) {
        super(red, green, blue, 1.0f);
    }

    public float getRed() {
        return this.x;
    }

    public float getGreen() {
        return this.y;
    }

    public float getBlue() {
        return this.z;
    }

    public float getAlpha() {
        return this.w;
    }

}
