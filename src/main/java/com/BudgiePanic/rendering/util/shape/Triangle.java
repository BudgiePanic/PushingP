package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Triangle shape primitive.
 * @author BudgiePanic
 */
public class Triangle extends BaseShape {

    protected final Tuple p1;
    protected final Tuple p2;
    protected final Tuple p3;
    protected final Tuple edge1;
    protected final Tuple edge2;
    protected final Tuple normal;
    protected static final Matrix4 defaultTransform = Matrix4.identity();
    protected static final Material defaultMaterial = Material.defaultMaterial();

    
    public Triangle(Tuple p1, Tuple p2, Tuple p3, Matrix4 transform, Material material) {
        super(transform, material);
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.edge1 = p2.subtract(p1);
        this.edge2 = p3.subtract(p1);
        this.normal = edge2.cross(edge1).normalize();
    }
    /**
     * Convienience constructor.
     *
     * @param p1
     * @param p2
     * @param p3
     * @param transform
     */
    public Triangle(Tuple p1, Tuple p2, Tuple p3, Matrix4 transform) {
        this(p1, p2, p3, transform, defaultMaterial);
    }

    /**
     * Convience constructor.
     * @param p1
     * @param p2
     * @param p3
     */
    public Triangle(Tuple p1, Tuple p2, Tuple p3) {
        this(p1, p2, p3, defaultTransform);
    }

    @Override
    public BoundingBox bounds() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bounds'");
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        // uses the Moller-Trumbore ray-triangle intersection algorithm
        final var dirCrossE2 = ray.direction().cross(edge2);
        final var determinant = edge1.dot(dirCrossE2);
        if (FloatHelp.compareFloat(0, Math.abs(determinant)) == 0) {
            // miss by parrallel ray
            return Optional.empty();
        }
        final var f = 1.0f / determinant;
        final var p1ToOrigin = ray.origin().subtract(p1);
        final var u = f * p1ToOrigin.dot(dirCrossE2); 
        if (FloatHelp.compareFloat(u, 0) == -1 || FloatHelp.compareFloat(u, 1) == 1) {
            // missed via the p1-p3 edge
            return Optional.empty();
        }
        final var originCrossEdge1 = p1ToOrigin.cross(edge1);
        final var v = f * ray.direction().dot(originCrossEdge1);
        if (FloatHelp.compareFloat(v, 0) == -1 || FloatHelp.compareFloat((u+v), 1) == 1) {
            return Optional.empty();
        }
        final var t = f * edge2.dot(originCrossEdge1);
        return Optional.of(List.of(new Intersection(t, this)));
    }

    @Override
    protected Tuple localNormal(Tuple point) { return normal; }
    
}
