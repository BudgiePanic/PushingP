package com.BudgiePanic.rendering.util.shape.composite;

import java.util.Collection;
import java.util.List;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import com.BudgiePanic.rendering.util.Tuple;
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

    @Override
    protected Tuple localNormal(Tuple point) { throw new UnsupportedOperationException("Composite shape does not support localNormal operation"); }

    @Override
    public synchronized BoundingBox bounds() {
        if (AABB == null) {
            BoundingBox box = new BoundingBox(makePoint(), makePoint());
            final var children = children();
            // the cube has 8 points [000,100,001,101,010,110,011,111]
            for (var shape : children) {
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
    public boolean contains(Shape shape) {
        return Parent.super.contains(shape);
    }

}