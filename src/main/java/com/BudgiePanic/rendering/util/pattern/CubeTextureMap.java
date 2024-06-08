package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Cube Texture Map applies six Pattern2D's to each face of a cube.
 * @author BudgiePanic
 */
public record CubeTextureMap(Pattern2D front, Pattern2D left, Pattern2D right, Pattern2D up, Pattern2D down, Pattern2D back, Matrix4 transform) implements Pattern {

    /**
     * Cube texture mapping pattern.
     * @param front
     *   The pattern on the front face of the cube.
     * @param left
     *   The pattern on the left face of the cube.
     * @param right
     *   The pattern on the right face of the cube.
     * @param up
     *   The pattern on the top face of the cube.
     * @param down
     *   The pattern on the bottom face of the cube.
     * @param back
     *   The pattern on the back face of the cube.
     * @param transform
     *   The transform to go from local object space to pattern space.
     *   Recommend keeping this as the identity because the pattern assumes points are in local object space.
     */
    public CubeTextureMap {}

    /**
     * Convenience constructor, uses identity transform.
     * @param front
     *   The pattern on the front face of the cube.
     * @param left
     *   The pattern on the left face of the cube.
     * @param right
     *   The pattern on the right face of the cube.
     * @param up
     *   The pattern on the top face of the cube.
     * @param down
     *   The pattern on the bottom face of the cube.
     * @param back
     *   The pattern on the back face of the cube.
     */
    public CubeTextureMap(Pattern2D front, Pattern2D left, Pattern2D right, Pattern2D up, Pattern2D down, Pattern2D back) {
        this(front, left, right, up, down, back, Matrix4.identity());
    }

    @Override
    public Color colorAt(Tuple point) {
        // hard coded reference to cube coordinate mapper
        final CoordinateMapper.Cube.Face face = CoordinateMapper.Cube.getFace(point);
        final double u = face.u(point);
        final double v = face.v(point);
        return switch (face) {
            case Front -> front.sample(u, v);
            case Back -> back.sample(u, v);
            case Left -> left.sample(u, v);
            case Right -> right.sample(u, v);
            case Up -> up.sample(u, v);
            case Down -> down.sample(u, v);
        };
    }
    
}
