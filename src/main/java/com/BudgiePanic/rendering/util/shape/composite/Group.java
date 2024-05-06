package com.BudgiePanic.rendering.util.shape.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.BudgiePanic.rendering.util.Pair;
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
     * Identity singleton to feed into subgroups.
     */
    private static final Matrix4 identity = Matrix4.identity();

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
    protected Optional<List<Intersection>> localIntersectI(Ray ray, Predicate<Shape> condition) {
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
    public boolean childrenContains(Shape shape) { return this.children.contains(shape); }

    /**
     * Divides the shapes within the group into two subgroups, left and right. Divided shapes are removed from this group.
     * Shapes that cannot be partitioned cleanly into the subsplit bounding boxes are not removed from this group.
     * Note: this method will mutate this group.
     * @return
     *   The shapes that should go into the left and right bounding box splits for this BVH.
     */
    protected Pair<List<Shape>, List<Shape>> partition() {
        final var subBounds = bounds().split(); // TODO promote this to composite shape parent class?
        final var left = subBounds.a();
        final var right = subBounds.b();
        List<Shape> lefts = null;
        List<Shape> rights = null;
        for (int i = 0; i < this.children.size(); i++) {
            final var shape = children.get(i);
            final var shapeBounds = shape.bounds().transform(shape.transform());
            if (left.contains(shapeBounds)) {
                if (lefts == null) { lefts = new ArrayList<>(); }
                lefts.add(shape);
                children.remove(i);
                i--;
            }
            else if (right.contains(shapeBounds)) {
                if (rights == null) { rights = new ArrayList<>(); }
                rights.add(shape);
                children.remove(i);
                i--;
            }
        }
        if (lefts == null) { lefts = List.of(); }
        if (rights == null) { rights = List.of(); }
        return new Pair<List<Shape>,List<Shape>>(lefts, rights);
    }

    /**
     * Creates a subvolume from the list of shapes and adds the subvolume to this Bounding Volume Hieararchy's list of child shapes
     * @param shapes
     *   The shapes to put in the sub volume.
     */
    protected void addChildGroup(List<Shape> shapes) {
        final var subShape = new Group(identity);
        subShape.children.addAll(shapes);
        this.children.add(subShape);
    }

    @Override
    public Shape divide(int threshold) {
        if (threshold <= children.size()) { 
            var splits = partition();
            if (!splits.a().isEmpty()) {
                addChildGroup(splits.a());
            }
            if (!splits.b().isEmpty()) {
                addChildGroup(splits.b());
            }    
        }
        for (final var shape : children) {
            shape.divide(threshold);
        }
        return this;
    }

}
