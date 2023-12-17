package com.BudgiePanic.rendering.util;

/**
 * Helper class with methods to convert between radians and degrees.
 * 
 * @author BudgiePanic
 */
public final class AngleHelp {
    
    private AngleHelp() {}

    /**
     * Convert angle in degrees to angle in radians.
     *
     * @param degrees
     *   The angle.
     * @return
     *   The angle in radians.
     */
    public final static float toRadians(float degrees) {
        // return (float) Math.toRadians(degrees);
        return (degrees / 180.0f) * (float) Math.PI;
    }

    /**
     * Convert angle in radians to angle in degrees.
     *
     * @param radians
     *   The angle.
     * @return
     *   The angle in degrees.
     */
    public final static float toDegrees(float radians) {
        // return Math.toDegrees(radians);
        return radians * (float)(180.0 / Math.PI);
    }
}
