package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * The Torus is a donut shape. It lies flat on the xy plane.
 * 
 * @see "intersecting a ray with a torus"
 *     http://cosinekitty.com/raytrace/chapter13_torus.html
 * 
 * @author BudgiePanic
 */
public class Torus extends BaseShape {

    /**
     * The distance from the toris origin to the torus defining circle.
     */
    protected final float radius;

    /**
     * The distance from the torus defining circle to the torus surface.
     */
    protected final float thickness;

    /**
     * Cached bounding box.
     */
    protected final BoundingBox aabb;

    /**
     * Canonical constructor. Create a new Torus.
     * @param transform
     *   World space to object space transform.
     * @param material
     *   Material the torus is made from.
     * @param radius
     *   The radius of the torus of the circle that passes through the center of the torus.
     * @param thickness
     *   The distance from the torus inner circle to the torus surface.
     */
    public Torus(Matrix4 transform, Material material, float radius, float thickness) { 
        super(transform, material); this.radius = radius; this.thickness = thickness; 
        if (thickness >= radius) {
            System.out.println("WARN: torus has thickness is larger than its radius, it won't have a hole");
        }
        final var toEdge = radius + thickness;
        this.aabb = new BoundingBox(
            makePoint(-toEdge, -thickness, -toEdge),
            makePoint(toEdge, thickness, toEdge)
        );
    }

    @Override
    public BoundingBox bounds() { return aabb; }

    @Override
    public boolean isSolid() { return true; }


    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'localIntersect'");
    }

    @Override
    protected Tuple localNormal(Tuple point) {
        // see: http://cosinekitty.com/raytrace/chapter13_torus.html
        float denominator = (float) Math.sqrt((point.x * point.x) + (point.y + point.y));
        if (FloatHelp.compareFloat(0, denominator) == 0) {
            // the point is not on the torus surface
            System.out.println("WARN: point " + point + " is not on the torus surface, adding small offset to avoid divide by zero");
            denominator = FloatHelp.epsilon;
        }
        final var alpha = radius / denominator;
        final var normal = makeVector((1 - alpha) * point.x, (1 - alpha) * point.y, point.z);
        return normal.normalize();
    }
    
}