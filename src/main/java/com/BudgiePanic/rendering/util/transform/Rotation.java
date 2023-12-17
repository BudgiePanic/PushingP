package com.BudgiePanic.rendering.util.transform;

import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Utility class to build rotation matrices
 * 
 * @author BudgiePanic
 */
public final class Rotation {
    
    private Rotation () {}

    /**
     * Helper method to convert degrees to radians.
     *
     * @param degrees
     *   The angle.
     * @return
     *   The angle in radians.
     */
    private static float toRadians(float degrees) {
        // return (float) Math.toRadians(degrees);
        return (degrees / 180.0f) * (float) Math.PI;
    }

    /**
     * Create new a rotation matrix with a pitch, yaw, and roll combined.
     *
     * @param pitch
     *   The up/down pitch. +ve values will pitch down.
     * @param yaw
     *   The left/right yaw. +ve values will rotate to the right.
     * @param roll
     *   The left/right bank angle. +ve values will roll to the left.
     * @return
     */
    public final static Matrix4 buildRotationMatrixDegrees(float pitch, float yaw, float roll) {
        return buildRotationMatrixRadians(toRadians(pitch), toRadians(yaw), toRadians(roll));
    }

    /**
     * Creates a new rotation matrix about the x axis.
     * 
     * @param radians
     *   The rotation angle in radians. +ve values causes downwards pitch.
     * @return
     *   A new rotation matrix.
     */
    public final static Matrix4 buildXRotationMatrix(float radians) {
        var cos = (float) Math.cos(radians);
        var sin = (float) Math.sin(radians);
        return Matrix4.buildMatrix(
            1f, 0f, 0f, 0f, 
            0f, cos, -sin, 0f, 
            0f, sin, cos, 0f, 
            0f, 0f, 0f, 1f
        );
    }

    /**
     * Creates a new rotation matrix about the y axis.
     *
     * @param radians
     *   The angle to rotate by in radians. +ve values yaw to the right.
     * @return
     *   A new rotation matrix.
     */
    public final static Matrix4 buildYRotationMatrix(float radians) {
        var cos = (float) Math.cos(radians);
        var sin = (float) Math.sin(radians);
        return Matrix4.buildMatrix(
            cos, 0f, sin, 0f, 
            0f, 1f, 0f, 0f, 
            -sin, 0f, cos, 0f, 
            0f, 0f, 0f, 1f
        );
    }

    /**
     * Creates a new rotation matrix about the z axis.
     *
     * @param radians
     *   The angle to rotate by in radians. +ve values roll to the right.
     * @return
     *   A new rotation matrix.
     */
    public final static Matrix4 buildZRotationMatrix(float radians) {
        var cos = (float) Math.cos(radians);
        var sin = (float) Math.sin(radians);
        return Matrix4.buildMatrix(
            cos, -sin, 0f, 0f, 
            sin, cos, 0f, 0f, 
            0f, 0f, 1f, 0f, 
            0f, 0f, 0f, 1f
        );
    }

    /**
     * Create new a rotation matrix with a pitch, yaw, and roll combined.
     *
     * @param pitch
     *   The up/down pitch. +ve values will pitch down.
     * @param yaw
     *   The left/right yaw. +ve values will rotate to the right.
     * @param roll
     *   The left/right bank angle. +ve values will roll to the left.
     * @return
     */
    public final static Matrix4 buildRotationMatrixRadians(float pitch, float yaw, float roll) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }
}
