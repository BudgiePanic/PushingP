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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.BaseShapeTest;
import com.BudgiePanic.rendering.util.shape.Cone;
import com.BudgiePanic.rendering.util.shape.Shape;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class GroupTest {

    static Matrix4 identity = Matrix4.identity();

    @Test
    void testGetShapes() {
        Group group = new Group(identity);
        var shapes = group.children();
        assertTrue(shapes.isEmpty());
    }

    @Test
    void testAddChildren() {
        var group = new Group(identity);
        var shape = new BaseShapeTest.TestShape(identity);
        group.addShape(shape);
        assertFalse(group.children.isEmpty());
        assertTrue(group.children.contains(shape));
        assertTrue(shape.parent().isPresent());
        assertEquals(group, shape.parent().get());
    }

    @Test
    void testIntersectionEmptyGroup() {
        var group = new Group(identity);
        var ray = new Ray(makePoint(), makeVector(0, 0, 1));
        var result = group.intersect(ray);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParentAttribute() {
        var group = new Group(identity);
        assertTrue(group.parent().isEmpty());
    }
    
    @Test
    void testNonEmptyGroupIntersection() {
        var group = new Group(identity);
        Shape shape1 = new Sphere(identity),
              shape2 = new Sphere(Transforms.identity().translate(0, 0, -3).assemble()),
              shape3 = new Sphere(Transforms.identity().translate(5, 0, 0).assemble());
        group.addShape(shape1);
        group.addShape(shape2);
        group.addShape(shape3);
        var ray = new Ray(makePoint(0,0,-5), makeVector(0, 0, 1));
        var result = group.localIntersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(4, intersections.size());
        assertEquals(shape2, intersections.get(0).shape());
        assertEquals(shape2, intersections.get(1).shape());
        assertEquals(shape1, intersections.get(2).shape());
        assertEquals(shape1, intersections.get(3).shape());
    }

    @Test
    void testGroupTransform() {
        var group = new Group(Transforms.identity().scale(2, 2, 2).assemble());
        group.addShape(new Sphere(Transforms.identity().translate(5, 0, 0).assemble()));
        var ray = new Ray(makePoint(10, 0, -10), makeVector(0, 0, 1));
        var result = group.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(2, intersections.size());
    }

    @Test
    void testRecursivePointTransformation() {
        float piOver2 = (float) (Math.PI / 2.0);
        var groupA = new Group(Transforms.identity().rotateY(piOver2).assemble());
        var groupB = new Group(Transforms.identity().scale(2, 2, 2).assemble());
        groupA.addShape(groupB);
        var shape = new Sphere(Transforms.identity().translate(5, 0, 0).assemble());
        groupB.addShape(shape);
        var result = shape.toObjectSpace(makePoint(-2, 0, -10));
        assertEquals(makePoint(0, 0, -1), result);
    }

    @Test
    void testRecusriveNormalTransformation() {
        float piOver2 = (float) (Math.PI / 2.0);
        float sqrt3over3 = (float) (Math.sqrt(3.0) / 3.0);
        var groupA = new Group(Transforms.identity().rotateY(piOver2).assemble());
        var groupB = new Group(Transforms.identity().scale(1, 2, 3).assemble());
        groupA.addShape(groupB);
        var shape = new Sphere(Transforms.identity().translate(5, 0, 0).assemble());
        groupB.addShape(shape);
        var result = shape.normalToWorldSpace(makeVector(sqrt3over3, sqrt3over3, sqrt3over3));
        var expected = makeVector(0.2857f, 0.4286f, -0.8571f);
        assertEquals(expected, result);
    }

    @Test
    void testGroupNormal() {
        float piOver2 = (float) (Math.PI / 2.0);
        var groupA = new Group(Transforms.identity().rotateY(piOver2).assemble());
        var groupB = new Group(Transforms.identity().scale(1, 2, 3).assemble());
        groupA.addShape(groupB);
        var shape = new Sphere(Transforms.identity().translate(5, 0, 0).assemble());
        groupB.addShape(shape);
        var result = shape.normal(makePoint(1.7321f, 1.1547f, -5.5774f));
        var expected = makeVector(0.2857f, 0.4286f, -0.8571f);
        assertEquals(expected, result);
    }

    @Test
    void testGroupSolid() {
        var group = new Group(identity);
        group.addShape(new Sphere(identity));
        assertTrue(group.isSolid());
        group.addShape(new Cone(identity, 1, 0, false));
        assertFalse(group.isSolid());
    }

    @Test
    void testGroupUsesAABB() {
        var shape = new Group(Transforms.identity().assemble());
        var temp = new BaseShapeTest.TestShape(Transforms.identity().assemble());
        shape.addShape(temp);
        var ray = new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1));
        shape.intersect(ray);
        assertTrue(temp.localIntersectRayResult != null);
    }

    @Test
    void testGroupUsesAABBA() {
        var shape = new Group(Transforms.identity().assemble());
        var temp = new BaseShapeTest.TestShape(Transforms.identity().assemble());
        shape.addShape(temp);
        var ray = new Ray(makePoint(0, 0, -5), makeVector(0, 1, 0));
        shape.intersect(ray);
        assertTrue(temp.localIntersectRayResult == null);
    }

    @Test
    void testPartition() {
        var shape1 = new Sphere(Transforms.identity().translate(-2, 0, 0).assemble());
        var shape2 = new Sphere(Transforms.identity().translate(2, 0, 0).assemble());
        var shape3 = new Sphere(Transforms.identity().assemble());
        var bvh = new Group(Transforms.identity().assemble());
        bvh.addShape(shape1);
        bvh.addShape(shape2);
        bvh.addShape(shape3);
        var result = bvh.partition();
        assertFalse(bvh.contains(shape1));
        assertFalse(bvh.contains(shape2));
        assertTrue(bvh.contains(shape3));
        assertTrue(result.a().contains(shape1));
        assertTrue(result.b().contains(shape2));
        assertFalse(result.a().contains(shape3));
        assertFalse(result.b().contains(shape3));
    }

    @Test
    void testSubGroupDivision() {
        var bvh = new Group(Transforms.identity().assemble());
        var shape1 = new Sphere(Transforms.identity().assemble());
        var shape2 = new Sphere(Transforms.identity().assemble());
        bvh.addChildGroup(List.of(shape1, shape2));
        assertEquals(1, bvh.children.size());
        var result = bvh.children.get(0);
        assertTrue(result instanceof Group);
        assertTrue(result.contains(shape1));
        assertTrue(result.contains(shape2));
    }

    @Test
    void testGroupDivide() {
        var shape1 = new Sphere(Transforms.identity().translate(-2, -2, 0).assemble());
        var shape2 = new Sphere(Transforms.identity().translate(-2, 2, 0).assemble());
        var shape3 = new Sphere(Transforms.identity().scale(4).assemble());
        var group = new Group(Transforms.identity().assemble());
        group.addShape(shape1);
        group.addShape(shape2);
        group.addShape(shape3);
        var result = group.divide(1);
        assertEquals(2, group.children.size(), result.toString());
        assertEquals(shape3, group.children.get(0));
        assertTrue(group.children.get(1) instanceof Group);
        Group inner = (Group) group.children.get(1);
        assertEquals(2, inner.children.size());
        assertTrue(inner.children.get(0) instanceof Group);
        assertTrue(inner.children.get(1) instanceof Group);
        assertEquals(1, ((Group)inner.children.get(0)).children.size());
        assertEquals(1, ((Group)inner.children.get(1)).children.size());
        assertEquals(shape1, ((Group)inner.children.get(0)).children.get(0));
        assertEquals(shape2, ((Group)inner.children.get(1)).children.get(0));
    }

    @Test
    void testGroupRecursiveDivide() {
        var shape1 = new Sphere(Transforms.identity().translate(-2, 0, 0).assemble());
        var shape2 = new Sphere(Transforms.identity().translate(2, 1, 0).assemble());
        var shape3 = new Sphere(Transforms.identity().translate(2, -1, 0).assemble());
        var group = new Group(Transforms.identity().assemble());
        group.addShape(shape1);
        group.addShape(shape2);
        group.addShape(shape3);
        var shape4 = new Sphere(Transforms.identity().assemble());
        var bigGroup = new Group(Transforms.identity().assemble());
        bigGroup.addShape(group);
        bigGroup.addShape(shape4);
        @SuppressWarnings("unused") // May update divide method later to return void...
        var result = bigGroup.divide(3);
        
        assertEquals(2, bigGroup.children.size());
        assertEquals(group, bigGroup.children.get(0));
        assertEquals(shape4, bigGroup.children.get(1));

        assertEquals(2, group.children.size());
        assertTrue(group.children.get(0) instanceof Group);
        assertTrue(group.children.get(1) instanceof Group);

        var left = (Group) group.children.get(0);
        var right = (Group) group.children.get(1);

        assertEquals(1, left.children.size());
        assertEquals(shape1, left.children.get(0));

        assertEquals(2, right.children.size());
        assertEquals(shape2, right.children.get(0));
        assertEquals(shape3, right.children.get(1));
    }
}
