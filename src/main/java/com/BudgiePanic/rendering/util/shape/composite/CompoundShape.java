package com.BudgiePanic.rendering.util.shape.composite;

import java.util.ArrayList;
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
 * A compound shape combines two shapes together.
 * 
 * @author BudgiePanic
 */
public class CompoundShape extends BaseShape implements Parent {

    protected final Shape left;
    protected final Shape right;
    protected final CompoundOperation operation;

    public CompoundShape(CompoundOperation operation, Shape left, Shape right, Matrix4 transform) {
        super(transform);
        this.left = left;
        this.right = right;
        this.operation = operation;
        this.left.setParent(this);
        this.right.setParent(this);
    }

    public Shape left() { return left; }

    public Shape right() { return right; }

    public CompoundOperation operation() { return operation; }

    @Override
    public BoundingBox bounds() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bounds'");
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'localIntersect'");
    }

    @Override
    protected Tuple localNormal(Tuple point) { throw new UnsupportedOperationException("Composite shape does not support localNormal operation"); }

    /**
     * Applies this compound shape's operation to remove intersections that violate the operation rule.
     * @param intersections
     *   The intersections to filter
     * @return
     *   Intersections that are valid according to this shape's combination rule.
     */
    protected List<Intersection> filter(List<Intersection> intersections) {
        assert !intersections.isEmpty();
        var inLeft = false;
        var inRight = false;
        List<Intersection> answer = new ArrayList<>(intersections.size() / 2);
        for (var intersection : intersections) {
            var isLeftHit = left.contains(intersection.shape());
            if (operation.isIntersectionValid(isLeftHit, inLeft, inRight)) {
                answer.add(intersection);
            }
            inLeft = isLeftHit ? !inLeft : inLeft;
            inRight = !isLeftHit ? !inRight : inRight;
        }
        return answer;
    }

    @Override
    public boolean childrenContains(Shape shape) { return left.contains(shape) || right.contains(shape); }

}
