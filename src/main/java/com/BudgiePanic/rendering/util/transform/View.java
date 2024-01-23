package com.BudgiePanic.rendering.util.transform;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Routines for creating a view transformation matrix.
 * 
 * The view transformation can be thought of as moving an 'eye' throughout a world to view it from different perspectives.
 * In reality the view matrix is applied to each object in the scene, "moving the world about a stationary eye".
 * 
 * @author BudgiePanic
 */
public final class View {
    private View() {}
    
    /**
     * Create a new view matrix that will orient a world object relative to the view that originates at 'from' and looks at 'to'.
     *
     * @param from
     *   The origin of the view matrix
     * @param to
     *   The direction that the view matrix looks at
     * @param up
     *   Which direction is up
     * @return
     *   A view matrix. 
     */
    public final static Matrix4 makeViewMatrix(Tuple from, Tuple to, Tuple up) {
        // algorithm:
          // compute forward vector
        Tuple forward = to.subtract(from).normalize();
          // compute left vector
        Tuple left = forward.cross(up.normalize());
          // compute true up vector from forward and left vectors
        Tuple trueUp = left.cross(forward);  
          // create orientation matrix
        Matrix4 orientation = Matrix4.buildMatrix(
            left.x, left.y, left.z, 0,
            trueUp.x, trueUp.y, trueUp.z, 0,
            -forward.x, -forward.y, -forward.z, 0,
            0, 0, 0, 1
        );
          // combine orientation matrix with a translation matrix
        return orientation.multiply(Translation.makeTranslationMatrix(-from.x, -from.y, -from.z));
    }
}
