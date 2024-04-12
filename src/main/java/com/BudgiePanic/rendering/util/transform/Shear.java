package com.BudgiePanic.rendering.util.transform;

import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Shear matrix builder utility class.
 * 
 * @author BudgiePanic
 */
public final class Shear {
    
    private Shear() {}

    /**
     * Build a new shear matrix.
     *
     * @param xy
     *   The shear of X proportional to Y
     * @param xz
     *   The shear of X proportional to Z
     * @param yx
     *   The shear of Y proportional to X
     * @param yz
     *   The shear of Y proportional to Z
     * @param zx
     *   The shear of Z proportional to X
     * @param zy
     *   The shear of Z proportional to Y
     * @return
     *   A shear matrix
     */
    public static final Matrix4 buildShearMatrix(double xy, double xz, double yx, double yz, double zx, double zy) {
        return Matrix4.buildMatrix(
            1f, xy, xz, 0f,
            yx, 1f, yz, 0f, 
            zx, zy, 1f, 0f,
            0f, 0f, 0f, 1f
        );
    }

}
