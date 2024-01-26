package com.BudgiePanic.rendering.util;

/**
 * Static methods that help with float imprecision.
 * 
 * @author BudgiePanic
 */
public final class FloatHelp {
    
    private FloatHelp() {}
    
    /**
     * The maximum difference in floats for them to be considered the same.
     */
    public final static float epsilon = 0.00001f;
    
    /**
     * checks if two floating point numbers are similar enough to each other.
     * Used instead of == to cope with floating point imprecision.
     * @param a 
     *   The first float to compare.
     * @param b
     *   The second float.
     * @return
     *    0 if the two numbers are within 0.00001 of each other.
     *    1 if a is larger than b.
     *   -1 if b is larger than a.
     */
    public static int compareFloat(float a, float b){
        float delta = (float) Math.abs(a - b);
        if (delta < epsilon) return 0;
        if (a < b) return -1;
        return 1;
    }
}
