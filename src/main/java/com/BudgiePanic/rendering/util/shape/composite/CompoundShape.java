/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.util.shape.composite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * A compound shape combines two shapes together.
 * 
 * @author BudgiePanic
 */
public class CompoundShape extends CompositeShape {

    protected final Shape left;
    protected final Shape right;
    /**
     * The rule that decides how the children are combined together.
     */
    protected final CompoundOperation operation;
    private final Collection<Shape> children;

    public CompoundShape(CompoundOperation operation, Shape left, Shape right, Matrix4 transform) {
        super(transform);
        this.left = left;
        this.right = right;
        this.operation = operation;
        this.left.setParent(this);
        this.right.setParent(this);
        this.children = List.of(this.left, this.right);
        if (!this.isSolid()) {
            System.out.println("WARN: compound shape contains non-solid shapes, intersection tests against this shape may have unexpected results!");
        }
    }

    protected Shape left() { return left; }

    protected Shape right() { return right; }

    protected CompoundOperation operation() { return operation; }

    @Override
    protected Optional<List<Intersection>> localIntersectI(Ray ray, Predicate<Shape> condition) {
        final var mapper = Intersection.buildIntersector(ray, condition);
        final Optional<List<Intersection>> leftIntersections = 
            condition.test(left) ? mapper.apply(left) : Optional.empty();
        final Optional<List<Intersection>> rightIntersections = 
            condition.test(right) ? mapper.apply(right) : Optional.empty();
        final var intersections = Stream.concat(leftIntersections.stream(), rightIntersections.stream()).
        flatMap(List::stream).sorted(Comparator.comparing(Intersection::a)).collect(Collectors.toList());
        if (intersections.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(filter(intersections));
    };

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

    @Override
    public Collection<Shape> children() { return children; }

    @Override
    public Shape divide(int threshold) {
        this.left.divide(threshold);
        this.right.divide(threshold);
        return this;
    }

}
