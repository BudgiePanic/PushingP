package com.BudgiePanic.rendering.util.shape.composite;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.BaseShape;
import com.BudgiePanic.rendering.util.shape.BoundingBox;
import com.BudgiePanic.rendering.util.shape.Parent;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * Composit Shape automatically computes its AABB using its children shapes.
 * 
 * @author BudgiePanic
 */
public abstract class CompositeShape extends BaseShape implements Parent {

    /**
     * Cached nullable bounding box.
     */
    protected BoundingBox AABB;

    public CompositeShape(Matrix4 transform) {
        super(transform);
        this.AABB = null;
    }

    /**
     * Get the children shapes of this composite shape.
     *
     * @return
     *   The shapes that composit this shape.
     */
    protected abstract Collection<Shape> children();

    /**
     * Perform local intersection test against the composite shape's children.
     * @param ray
     *     The ray to test with.
     * @param condition
     *     Perform intersection tests against shapes matching this condition.
     * @return
     *     A list of ray-shape intersections made on the composite shapes children, if any.
     */
    protected abstract Optional<List<Intersection>> localIntersectI(Ray ray, Predicate<Shape> condition);

    /**
     * Perform local intersection tests on children shapes.
     * @param ray
     *   The ray to test with.
     * @param condition
     *   perform intersection tests against shapes matching this condition
     * @return
     *   A list of ray-shape intersections made on the composite shapes children, if any.
     */
    protected final Optional<List<Intersection>> localIntersect(Ray ray, Predicate<Shape> condition) {
        if (children().isEmpty() || !bounds().intersect(ray)) { // AABB check
            return Optional.empty();
        }
        return localIntersectI(ray, condition);
    };

    @Override
    public Optional<List<Intersection>> intersect(Ray ray, Predicate<Shape> inclusionCondition) {
        var transformInverse = this.transform().inverse();
        var rayInObjectSpace = ray.transform(transformInverse);
        return localIntersect(rayInObjectSpace, inclusionCondition);
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) { return localIntersect(ray, (s)->true); }

    @Override
    protected Tuple localNormal(Tuple point) { throw new UnsupportedOperationException("Composite shape does not support localNormal operation"); }

    @Override
    public synchronized BoundingBox bounds() {
        if (AABB == null) {
            BoundingBox box = null;
            final var children = children();
            // the cube has 8 points [000,100,001,101,010,110,011,111]
            for (var shape : children) {
                final BoundingBox localAABB = shape.bounds().transform(shape.transform());
                if (box == null) {
                    box = localAABB;
                } else {
                    if (!box.contains(localAABB.maximum())) {
                        box = box.grow(localAABB.maximum());
                    }
                    if (!box.contains(localAABB.minimum())) {
                        box = box.grow(localAABB.minimum());
                    }
                }
            }
            AABB = box;
        }
        return AABB;
    }
    
    @Override
    public boolean contains(Shape shape) {
        return Parent.super.contains(shape);
    }

    @Override
    public boolean isSolid() { return this.children().stream().allMatch(Shape::isSolid); }

}
