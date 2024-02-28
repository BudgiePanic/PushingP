package com.BudgiePanic.rendering.util.shape;

import java.util.NoSuchElementException;

import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * A triangle shape that uses normal interpolation to fake the appearance of a smooth surface.
 * 
 * The triangle will interpolate the normal when it is supplied the UV coordinate of the intersection on the triangle surface
 * by calling Shape::normal(Tuple, Intersection)
 * 
 * @author BudgiePanic
 */
public class SmoothTriangle extends Triangle {

    protected final Tuple normal1;
    protected final Tuple normal2;
    protected final Tuple normal3;

    /**
     * Build a new smooth triangle.
     * @param p1
     *   first triangle vertex
     * @param p2
     *   second triangle vertex
     * @param p3
     *   third triangle vertex
     * @param n1
     *   normal of first vertex
     * @param n2
     *   normal of second vertex
     * @param n3
     *   vertex of third vertex
     * @param transform
     *   transform to triangle space
     * @param material
     *   triangle material for shading
     */
    public SmoothTriangle(Tuple p1, Tuple p2, Tuple p3, Tuple n1, Tuple n2, Tuple n3, Matrix4 transform, Material material) {
        super(p1, p2, p3, transform, material);
        this.normal1 = n1;
        this.normal2 = n2;
        this.normal3 = n3;
    }

    /**
     * Convience constructor. Builds a smooth triangle with the default material.
     * @param p1
     * @param p2
     * @param p3
     * @param n1
     * @param n2
     * @param n3
     * @param transform
     */
    public SmoothTriangle(Tuple p1, Tuple p2, Tuple p3, Tuple n1, Tuple n2, Tuple n3, Matrix4 transform) {
        this(p1, p2, p3, n1, n2, n3, transform, defaultMaterial);
    }

    /**
     * Convience constructor. Builds a smooth triangle with the default transform.
     * @param p1
     * @param p2
     * @param p3
     * @param n1
     * @param n2
     * @param n3
     * @param material
     */
    public SmoothTriangle(Tuple p1, Tuple p2, Tuple p3, Tuple n1, Tuple n2, Tuple n3, Material material) {
        this(p1, p2, p3, n1, n2, n3, defaultTransform, material);
    }

    /**
     * Convience constructor. Builds a smooth triangle with the default transform and default material.
     * @param p1
     * @param p2
     * @param p3
     * @param n1
     * @param n2
     * @param n3
     */
    public SmoothTriangle(Tuple p1, Tuple p2, Tuple p3, Tuple n1, Tuple n2, Tuple n3) {
        this(p1, p2, p3, n1, n2, n3, defaultTransform, defaultMaterial);
    }
    
    @Override 
    protected Tuple localNormal(Tuple point, Intersection intersection) {
        return intersection.uv().map(uv -> {
            final float u = uv.a(), v = uv.b();
            return normal2.multiply(u).
                   add(normal3.multiply(v)).
                   add(normal1.multiply(1f - u - v));
        }).orElseThrow(()->new NoSuchElementException("""
            intersection contained no UV coordinates when smooth triangle tried to access them
        """));
    }

    @Override 
    protected Tuple localNormal(Tuple point) { throw new UnsupportedOperationException("""
        use Shape::normal(Tuple, Intersection) for smooth triangle normal, did you mean to use a standard triangle?     
    """);
    }

    public Tuple normal1() { return normal1; }
    public Tuple normal2() { return normal2; }
    public Tuple normal3() { return normal3; }
    
}
