package com.BudgiePanic.rendering.util.transform;

import com.BudgiePanic.rendering.util.matrix.Matrix4;

public final class Scale {
    
    private Scale() {}

    /**
     * Build a new scale matrix.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static final Matrix4 makeScaleMatrix(double x, double y, double z) {
        var identity = Matrix4.identity();
        identity.matrix[0][0] = x;
        identity.matrix[1][1] = y;
        identity.matrix[2][2] = z;
        return identity;
    }

    /**
     * Build a new reflection matrix. Reflects points about the axis that are set to true.
     * If all parameters are false, the identity matrix is returned.
     * 
     * @param x
     *   Should reflect x axis
     * @param y
     *   Should reflect y axis
     * @param z
     *   Should reflect z axis
     * @return
     *   A reflection scale matrix.
     */
    public static final Matrix4 makeReflectMatrix(boolean x, boolean y, boolean z) {
        return makeScaleMatrix(x ? -1.0 : 1.0, y ? -1.0 : 1.0, z ? -1.0 : 1.0);
    }

}
