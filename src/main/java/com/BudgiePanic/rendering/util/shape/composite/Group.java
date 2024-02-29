package com.BudgiePanic.rendering.util.shape.composite;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.BaseShape;
import com.BudgiePanic.rendering.util.shape.BoundingBox;
import com.BudgiePanic.rendering.util.shape.Parent;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * A group is a shape comprised of more shapes.
 * 
 * @author BudgiePanic
 */
public class Group extends BaseShape implements Parent {
    
    /**
     * A mutable collection of shapes.
     */
    protected final List<Shape> children;

    /**
     * Creates a new empty group with no parent.
     *
     * @param transform
     *   The transform to enter group space.
     */
    public Group(Matrix4 transform) {
        super(transform);
        this.children = new ArrayList<>();
        this.AABB = null;
    }

    /**
     * Gets the shapes that are part of this group.
     * @return
     *   The shapes that belong to this group.
     */
    public List<Shape> getShapes() {
        return Collections.unmodifiableList(this.children);
    }
    
    /**
     * Adds the shape to this group and sets the group's parent to 'this'.
     * @param shape
     *   The shape to add to the group.
     */
    public void addShape(Shape shape) {
        this.AABB = null;
        this.children.add(shape);
        shape.setParent(this);
    }

    public void removeShape(Shape shape) {
        this.AABB = null;
        var removed = this.children.remove(shape);
        if (removed) {
            shape.setParent(null);
        }
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        if (!bounds().intersect(ray)) {
            return Optional.empty();
        }
        List<Intersection> result = null;
        for (var child : children) {
            var intersect = child.intersect(ray);
            if (intersect.isPresent()) {
                if (result == null) result = new ArrayList<>();
                result.addAll(intersect.get());
            }
        }
        if (result != null) result.sort(Comparator.comparing(Intersection::a));
        return Optional.ofNullable(result);
    }

    @Override
    protected Tuple localNormal(Tuple point) { throw new UnsupportedOperationException("Shape group does not support localNormal operation"); }

    protected BoundingBox AABB;

    @Override
    public synchronized BoundingBox bounds() {
        if (AABB == null) {
            BoundingBox box = new BoundingBox(makePoint(), makePoint());
            // the cube has 8 points [000,100,001,101,010,110,011,111]
            for (var shape : this.children) {
                final BoundingBox localAABB = shape.bounds();
                final var aabbMin = localAABB.minimum();
                final var aabbMax = localAABB.maximum();
                final var transform = shape.transform();
                // find the AABB points in 'group space'
                Tuple _000 = transform.multiply(new Tuple(aabbMax.x, aabbMin.y, aabbMin.z)); // MAX MIN 
                Tuple _100 = transform.multiply(aabbMin); // MIN                                MIN MIN
                Tuple _001 = transform.multiply(new Tuple(aabbMax.x, aabbMin.y, aabbMax.z)); // MAX MAX
                Tuple _101 = transform.multiply(new Tuple(aabbMin.x, aabbMin.y, aabbMax.z)); // MIN MAX

                Tuple _010 = transform.multiply(new Tuple(aabbMax.x, aabbMax.y, aabbMin.z)); // MAX MIN
                Tuple _110 = transform.multiply(new Tuple(aabbMin.x, aabbMax.y, aabbMin.z)); // MIN MIN
                Tuple _011 = transform.multiply(aabbMax); // MAX                                MAX MAX
                Tuple _111 = transform.multiply(new Tuple(aabbMin.x, aabbMax.y, aabbMax.z)); // MIN MAX
                var points = List.of(_000, _001, _010, _011, _100, _101, _110, _111);
                // check if we need to grow the AABB extents to contain the points
                for (var point : points) {
                    if (!box.contains(point)) {
                        box = box.grow(point);
                    }
                }
            }
            AABB = box;
        }
        return AABB;
    }

    @Override
    public boolean childrenContains(Shape shape) { return this.children.contains(shape); }

}
