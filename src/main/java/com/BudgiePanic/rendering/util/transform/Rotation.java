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
     * Creates a new rotation matrix about the x axis.
     * 
     * @param radians
     *   The rotation angle in radians. +ve values causes downwards pitch.
     * @return
     *   A new rotation matrix.
     */
    public final static Matrix4 buildXRotationMatrix(double radians) {
        var cos = Math.cos(radians);
        var sin = Math.sin(radians);
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
    public final static Matrix4 buildYRotationMatrix(double radians) {
        var cos = Math.cos(radians);
        var sin = Math.sin(radians);
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
    public final static Matrix4 buildZRotationMatrix(double radians) {
        var cos = Math.cos(radians);
        var sin = Math.sin(radians);
        return Matrix4.buildMatrix(
            cos, -sin, 0f, 0f, 
            sin, cos, 0f, 0f, 
            0f, 0f, 1f, 0f, 
            0f, 0f, 0f, 1f
        );
    }
}
