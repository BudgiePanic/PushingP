package com.BudgiePanic.rendering.util.raster;

import com.BudgiePanic.rendering.scene.BasePerspectiveCamera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Converts 3D line segments into 2D canvas points, with optional line depth checks.
 *
 * @author BudgiePanic
 */
public class LineProjector {
    
    /**
     * Projects a 3D line segment into a 2D surface.
     * @param from
     * @param to
     * @param camera
     * @return
     *   The coordinates of the start and end point in 2D space of the line segment (x1, y1, x2, y2)
     */
    protected int[] project(Tuple from, Tuple to, BasePerspectiveCamera camera) {
        return null;
    }

    /**
     * Projects a 3D line segment into a 2D surface with depth occlusion.
     * @param from
     * @param to
     * @param camera
     * @param world
     * @return
     */
    protected int[][] project(Tuple from, Tuple to, BasePerspectiveCamera camera, World world) {
        return null;
    }

}
