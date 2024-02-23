package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Base shape converts incoming locations from world space to object space before passing calculations off to shape implementations.
 * 
 * NOTE: in the future, if we want to have scene objects that are composed of components
 *       then the material property can be stripped from the sphere. but currently we are 
 *       going to enforce that all spheres (shapes) have to have a material.
 * 
 * @author BudgiePanic
 */
public abstract class BaseShape implements Shape {

    /**
     * All shapes have the same origin in OBJECT space.
     * Rays will be converted from world space to object space before intersection tests 
     * are performed. Making a singleton here to avoid allocating a new object everytime an intersection test
     * is performed.
     */
    protected static final Tuple origin = Tuple.makePoint();

    /**
     * Information needed to convert locations from world space to the object's local space
     */
    protected final Matrix4 transform;

    /**
     * information needed to light the shape in the scene
     */
    protected final Material material;

    /**
     * The shape tree that this shape belongs to, if any.
     * Mutable field because the group will set itself as the parent when this shape is added to a group,
     * which may happen after the object is constructed.
     */
    protected Optional<Group> parent;

    /**
     * A base shape
     *
     * @param transform
     *   The shape's transform
     * @param material
     *   The shape's material
     */
    public BaseShape(Matrix4 transform, Material material) {
        if (transform == null) throw new IllegalArgumentException("sphere transform cannot be null");
        if (material == null) throw new IllegalArgumentException("sphere material cannot be null");
        this.transform = transform;
        this.material = material;
        this.parent = Optional.empty();
    }

    /**
     * Create a base shape with a default material
     * 
     * @param transform
     *   The shape's transform
     */
    public BaseShape(Matrix4 transform) {
        this(transform, Material.defaultMaterial());
    }

    
    @Override
    public Optional<List<Intersection>> intersect(Ray ray) {
        if (ray == null) throw new IllegalArgumentException("ray is null");
        // pass the ray through the shape transform then call local intersect
        var transformInverse = this.transform().inverse();
        var rayInObjectSpace = ray.transform(transformInverse);
        return localIntersect(rayInObjectSpace);
    }
    
    @Override
    public Tuple normal(Tuple point) {
        if (point == null) throw new IllegalArgumentException("point is null");
        // optimized technique described in in Jamis Buck's book The Ray Tracer Challenge
        var inverse = this.transform.inverse();
        var objectSpacePoint = inverse.multiply(point); // the point in object space
        var localNormal = localNormal(objectSpacePoint);
        var worldNormal = inverse.transpose().multiply(localNormal); // the normal in world space (optimization here)
        // hacky step here due to the optimization
        return Tuple.makeVector(worldNormal.x, worldNormal.y, worldNormal.z).normalize();
    }
    
    @Override
    public Matrix4 transform() {
        return this.transform;
    }

    @Override
    public Material material() {
        return this.material;
    }
    
    @Override
    public Optional<Group> parent() {
        return this.parent;
    }

    @Override
    public void setParent(Group group) {
        this.parent = Optional.ofNullable(group);
    }

    /**
     * Determine the distance between the ray origin and intersection points with this shape, if any.
     * 
     * @param ray
     *   A ray that has been transformed to object space.
     * @return
     *   A list of distances to intersection points along the ray, from the ray origin. May be empty.
     */
    protected abstract Optional<List<Intersection>> localIntersect(Ray ray);

    /**
     * Calcualte the normal of a point on the surface of the shape
     *
     * @param point
     *   the shape
     * @return
     *   the vector normal of the shape at point location
     */
    protected abstract Tuple localNormal(Tuple point);

    @Override
    public String toString() {
        return String.format("%s[transform=%s, material=%s]", this.getClass().getSimpleName(), this.transform.toString(), this.material.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(transform, material);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseShape other = (BaseShape) obj;
        if (transform == null) {
            if (other.transform != null) {
                return false;
            }
        } else if (!transform.equals(other.transform)) {
            return false;
        }
        if (material == null) {
            if (other.material != null) {
                return false;
            }
        } else if (!material.equals(other.material)) {
            return false;
        }
        return true;
    }

    


}
