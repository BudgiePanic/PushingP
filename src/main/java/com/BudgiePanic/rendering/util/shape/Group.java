package com.BudgiePanic.rendering.util.shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * A group is a shape comprised of more shapes.
 * 
 * @author BudgiePanic
 */
public class Group extends BaseShape {
    
    /**
     * A mutable collection of shapes.
     */
    protected final Collection<Shape> children;

    /**
     * Creates a new empty group with no parent.
     *
     * @param transform
     *   The transform to enter group space.
     */
    public Group(Matrix4 transform) {
        super(transform);
        this.children = new ArrayList<>();
    }

    /**
     * Gets the shapes that are part of this group.
     * @return
     *   The shapes that belong to this group.
     */
    public Collection<Shape> getShapes() {
        return Collections.emptyList();
    }
    
    /**
     * Adds the shape to this group and sets the group's parent to 'this'.
     * @param shape
     *   The shape to add to the group.
     */
    public void addShape(Shape shape) {
        this.children.add(shape);
        shape.setParent(this);
    }

    public void removeShape(Shape shape) {
        var removed = this.children.remove(shape);
        if (removed) {
            shape.setParent(null);
        }
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
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

}
