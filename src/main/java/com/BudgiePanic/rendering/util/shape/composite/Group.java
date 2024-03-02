package com.BudgiePanic.rendering.util.shape.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * A group is a shape comprised of more shapes.
 * 
 * @author BudgiePanic
 */
public class Group extends CompositeShape {
    
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
    }

    /**
     * Gets the shapes that are part of this group.
     * @return
     *   The shapes that belong to this group.
     */
    @Override
    public List<Shape> children() {
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
    protected Optional<List<Intersection>> localIntersect(Ray ray, Predicate<Shape> condition) {
        if (!bounds().intersect(ray)) {
            return Optional.empty();
        }
        List<Intersection> result = null; // lazilly initialize intersect list to avoid excessive list creation
        if (children.isEmpty()) { return Optional.empty(); }
        final var mapper = Intersection.buildIntersector(ray, condition);
        for (var child : children) {
            if (!condition.test(child)) { continue; }
            var intersect = mapper.apply(child);
            if (intersect.isPresent()) {
                if (result == null) { result = new ArrayList<>(); }
                result.addAll(intersect.get());
            }
        }
        if (result != null) { result.sort(Comparator.comparing(Intersection::a)); }
        return Optional.ofNullable(result);
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) { return localIntersect(ray, (s) -> true); };

    @Override
    public boolean childrenContains(Shape shape) { return this.children.contains(shape); }


}
