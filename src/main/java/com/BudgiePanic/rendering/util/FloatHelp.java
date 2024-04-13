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
    public final static double epsilon = 0.0001;
    
    /**
     * checks if two floating point numbers are similar enough to each other.
     * Used instead of == to cope with floating point imprecision.
     * @param a 
     *   The first float to compare.
     * @param b
     *   The second float.
     * @return
     *    0 if the two numbers are within 0.0001 of each other.
     *    1 if a is larger than b.
     *   -1 if b is larger than a.
     */
    public static int compareFloat(double a, double b){

        final boolean aIsInfinite = Double.isInfinite(a), bIsInfinite = Double.isInfinite(b);
        if (aIsInfinite || bIsInfinite) {
            if (a < b) {
                return -1;
            } else if (a > b) {
                return 1;
            } else {
                return 0;
            }
        }

        double delta = Math.abs(a - b);
        if (delta < epsilon) return 0;
        if (a < b) return -1;
        return 1;
    }
}
