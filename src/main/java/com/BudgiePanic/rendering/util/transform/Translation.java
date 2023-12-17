package com.BudgiePanic.rendering.util.transform;

import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * A translation matrix transform builder.
 * 
 * @author BudgiePanic
 */
public class Translation {

    /**
     * Build a new translation matrix.
     *
     * @param x
     *   x offset
     * @param y
     *   y offset
     * @param z
     *   z offset
     * @return
     *   a new translation matrix
     */
    public static Matrix4 makeTranslationMatrix(float x, float y, float z) {
        var identity = Matrix4.identity();
        identity.matrix[0][3] = x;
        identity.matrix[1][3] = y;
        identity.matrix[2][3] = z;
        return identity;
    }
}
