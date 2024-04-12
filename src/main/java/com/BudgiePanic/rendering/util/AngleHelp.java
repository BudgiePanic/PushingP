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
    public final static double toRadians(double degrees) {
        // return (float) Math.toRadians(degrees);
        return (degrees / 180.0) * Math.PI;
    }

    /**
     * Convert angle in radians to angle in degrees.
     *
     * @param radians
     *   The angle.
     * @return
     *   The angle in degrees.
     */
    public final static double toDegrees(double radians) {
        // return Math.toDegrees(radians);
        return radians * (180.0 / Math.PI);
    }
}
