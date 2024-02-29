package com.BudgiePanic.rendering.util.shape.composite;

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
    protected final Operation operation;

    public CompoundShape(Operation operation, Shape left, Shape right, Matrix4 transform) {
        super(transform);
        this.left = left;
        this.right = right;
        this.operation = operation;
        this.left.setParent(this);
        this.right.setParent(this);
    }

    public Shape left() { return left; }

    public Shape right() { return right; }

    public Operation operation() { return operation; }

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
    protected Tuple localNormal(Tuple point) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'localNormal'");
    }

}
