package com.BudgiePanic.rendering.util;

/**
 * Static methods that help with float imprecision.
 * 
 * @author BudgiePanic
 */
public final class FloatHelp {
    private FloatHelp() {}
    /**
     * checks if two floating point numbers are similar enough to each other.
     * Used instead of == to cope with floating point imprecision.
     * @param a 
     *   The first float to compare.
     * @param b
     *   The second float.
     * @return
     *    0 if the two numbers are within 0.0000001 of each other.
     *    1 if a is larger than b.
     *   -1 if b is larger than a.
     */
    public static int compareFloat(float a, float b){
        // Uses 6 decimal places of precision for the check.
        final float epsilon = 0.0000001f;
        if (Math.abs(a - b) < epsilon) return 0;
        if (a < b) return -1;
        return 1;
    }
}
